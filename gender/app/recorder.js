(function(window){

  var WORKER_PATH = 'recorderWorker.js';

  var Recorder = function(source, cfg){
    var config = cfg || {};
    var bufferLen = config.bufferLen || 16384;
    this.context = source.context;
    this.node = this.context.createJavaScriptNode(bufferLen, 2, 2);
    var worker = new Worker(config.workerPath || WORKER_PATH);
    worker.postMessage({
      command: 'init',
      config: {
        sampleRate: this.context.sampleRate
      }
    });
    var recording = false, currCallback;
	var currLength = 0, buffer = [];
	var length = 16384*6;

    this.node.onaudioprocess = function(e){
      
	  //Record about 2 secs of audio
	  if (!recording || currLength>length) return; 
	  else {
		currLength += 16384; 
		buffer = Float32Concat(buffer, e.inputBuffer.getChannelData(0));
		
		//"in place" decimation, when we filled the buffer
		if(currLength>length){
			var filtered = lowPass8192(buffer);
			for (var i = 0, j=0; i < filtered.length; i=i+3, j++) {
				filtered[j] =	filtered[i]*1000;
				filtered[j] = filtered[j].toPrecision(6);			
			}	
			/*
			worker.postMessage({
								command: 'record',
								buffer: [
									e.inputBuffer.getChannelData(0),
									e.inputBuffer.getChannelData(1)
								]
								});
								
			worker.postMessage({
								command: 'exportWAV',
								type: type
								});
			
			*/
			//post samples as strings!
			postSamples(filtered.slice(1, 32000));
		}
	  }
	  
    }
	
	function postSamples(samples){		
			
		var fd = new FormData();
		fd.append('the_matrix', '[' + samples.toString() + ']');
		fd.append('the_double', '0.00003232');
		fd.append('$', 'gender');
		$.ajax({
					type: 'POST',
					url: "http://ec2-54-242-178-145.compute-1.amazonaws.com/index.php",
					//$: 'gender',
					data: fd,
					processData: false,
					contentType: false
					}).done(function(data) {
						alert(data.match("Extracted.*") + data.match("Speaker.*") + data.match("elapsed.*") + " seconds");
					});

	}
    
	function lowPass8192(signal){
		var taps = [3.0931e-03, -1.2191e-04, -1.4003e-02, -2.9778e-02, 6.2382e-04, 1.1373e-01, 2.6119e-01, 3.3053e-01, 2.6119e-01, 1.1373e-01, 6.2382e-04, -2.9778e-02, -1.4003e-02,  -1.2191e-04, 3.0931e-03];
		var filtered = new Array();
		for(var ix = 0; ix<signal.length; ix++){
			filtered[ix] = 0.0;
			for (var tx=0; tx<taps.length; tx++)
				filtered[ix] += taps[tx]*signal[ix + tx];
		}
		return filtered;		
	}
	
	function Float32Concat(first, second)
	{
    var firstLength = first.length;
    var result = new Float32Array(firstLength + second.length);

    result.set(first);
    result.set(second, firstLength);

    return result;
	}
	
	
    this.configure = function(cfg){
      for (var prop in cfg){
        if (cfg.hasOwnProperty(prop)){
          config[prop] = cfg[prop];
        }
      }
    }

    this.record = function(){
      recording = true;
    }

    this.stop = function(){
      recording = false;
    }

    this.clear = function(){
      worker.postMessage({ command: 'clear' });
    }

    this.getBuffer = function(cb) {
      currCallback = cb || config.callback;
      worker.postMessage({ command: 'getBuffer' })
    }

    this.exportWAV = function(cb, type){
      currCallback = cb || config.callback;
      type = type || config.type || 'audio/wav';
      if (!currCallback) throw new Error('Callback not set');
      worker.postMessage({
        command: 'exportWAV',
        type: type
      });
    }

    worker.onmessage = function(e){
      var blob = e.data;
      currCallback(blob);
    }

    source.connect(this.node);
    this.node.connect(this.context.destination);    //this should not be necessary
  };

  Recorder.forceDownload = function(blob, filename){
    var url = (window.URL || window.webkitURL).createObjectURL(blob);
    var link = window.document.createElement('a');
    link.href = url;
    link.download = filename || 'output.wav';
    var click = document.createEvent("Event");
    click.initEvent("click", true, true);
    link.dispatchEvent(click);
  }

  window.Recorder = Recorder;

})(window);
