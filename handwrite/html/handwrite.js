var ctx, w, h;
var canvas, drawing=false, prevX=0, currX=0, prevY=0, currY=0, dot_flag=false;
var timer;
var clear = false;
var maxX = 0, maxY = 0, minX = Number.MAX_VALUE, minY = Number.MAX_VALUE;
var trainDigit = 0;
var scaled;
var letfound;
var person = "someGuy";

function Handwrite(canvas, letterFound, wordFound) {
	
	letfound = letterFound;	
	this.canvas = canvas;
	this.letterHandle = letterFound;
	this.wordHandler = wordFound;
	ctx=canvas.getContext("2d");
	w=canvas.width;
    	h=canvas.height;
	canvas.addEventListener("mousemove", function(e){ findxy('move',e)}, false);
    	canvas.addEventListener("mousedown", function(e){ findxy('down',e)}, false);
    	canvas.addEventListener("mouseup", function(e){ findxy('up',e)}, false);
        canvas.addEventListener("mouseout", function(e){ findxy('out',e)}, false);
}

function draw()
{
    ctx.beginPath();
    ctx.moveTo(prevX,prevY);
    ctx.lineTo(currX,currY);
    ctx.strokeStyle=x;
    ctx.lineWidth=y;
    ctx.stroke();
    ctx.closePath();
}
function erase()
{
    var m=confirm("Want to clear");
    if(m)
    {
        ctx.clearRect(0,0,w,h);
        document.getElementById("canvasimg").style.display="none";
    }
}


/*this one is used to send training data to the server*/
function send()
{
	var a = document.getElementById('trainDigit');
	trainDigit = a.value;
        person=document.getElementById('person').value;

        postImage(runLengthEncodeColumn(scaled));
	maxX = 0, maxY = 0, minX = Number.MAX_VALUE, minY = Number.MAX_VALUE;

 
 }
function findxy(res,e)
{
    //Press mouse down
    if(res=='down') {
        prevX=currX;prevY=currY;
        currX=e.clientX-canvas.offsetLeft;
        currY=e.clientY-canvas.offsetTop;
	//Start timer
        clearTimeout(timer);
        timer = setTimeout(processBuffer, 600);
        clear = false;

        drawing=true;
        dot_flag=true;
     	
        if(dot_flag){

            ctx.beginPath();
            ctx.fillStyle=x;
            ctx.fillRect(currX,currY,2,2);
            ctx.closePath();
            dot_flag=false;
         }
    }
            
    //Press mouse up
    if(res=='up'||res=="out"){
                drawing=false; 
    } 
  
    //Move mouse
    if(res=='move') {
        if(drawing) {
            //if timeout hasn't fired, reset it.
            if(!clear) {
                clearTimeout(timer);
                timer = setTimeout(processBuffer, 700);
				}
                prevX=currX;
                prevY=currY;
                currX=e.clientX-canvas.offsetLeft;
                currY=e.clientY-canvas.offsetTop;
				
                maxX = currX>maxX ? currX : maxX;
                maxY = currY>maxY ? currY : maxY;
                minX = currX<minX ? currX : minX;
                minY = currY<minY ? currY : minY;
				
                draw();
        }
    }
}
function max(x, y) {
	return x>y? x: y;
}

function processBuffer() {
	
	clear = true;
	/*frame the image*/
	var width = maxX-minX;
	var height = maxY-minY;
	var frame = 0.20;
	minX = Math.floor(minX - frame*width);
	maxX = Math.floor(maxX + frame*width);
	minY = Math.floor(minY - frame*height);
	maxY = Math.floor(maxY + frame*height);
	
	width = maxX-minX;
	height = maxY-minY;
	
	
	var diff = height-width;
	//TODO: this image data will have to come from the current buffer
	var imgData = ctx.getImageData(Math.floor(minX-diff/2), minY, height, height);
	//TODO: this factor should be changed according to imgData
	
	//var scaled = scaleImageData(imgData,factor);
	scaled = nn_resize(imgData, height, height, 20, 20);
	//TODO: this step should be removed, used for debugging
	ctx.putImageData(scaled, 10, 370);  
	
	if(trainDigit == 0) {
		postImage(runLengthEncodeColumn(scaled));
		maxX = 0, maxY = 0, minX = Number.MAX_VALUE, minY = Number.MAX_VALUE;
	}
}


/*takes an imageData and encodes it using run length enconding, return an array of tuples (numZeroes, value)*/
function runLengthEncodeRow(imageData) {
	
	var encoded = [];
	var zeroCount = 0;
	var index = 0;
	
	for(var i = 3; i<imageData.data.length; i = i+4){
		if(imageData.data[i] == 0)
			zeroCount++;
		else {
			encoded[index++] = zeroCount;
			encoded[index++] = 1;//imageData.data[i];
			zeroCount = 0;
		}
	}
	return encoded;
}


/*takes an imageData and encodes it using run length enconding, return an array of tuples (numZeroes, value)*/
function runLengthEncodeColumn(imageData) {
	
	var encoded = [];
	var zeroCount = 0;
	var index = 0;
	
	for(var j = 3; j<80 ; j=j+4){
		for(var i = j; i<imageData.data.length; i = i+80){
			if(imageData.data[i] == 0)
				zeroCount++;
			else {
				encoded[index++] = zeroCount;
				encoded[index++] = 1;//imageData.data[i];
				zeroCount = 0;
			}
		}
	}
	return encoded;
}

function postImage(samples){		
			
    var fd = new FormData();
    fd.append('image', '[' + samples.toString() + ']');
    fd.append('trainDigit', trainDigit.toString());
    fd.append('$', 'draw');
    fd.append('person', person);
    $.ajax({
					type: 'POST',
					url: "/index.php",
					data: fd,
					processData: false,
					contentType: false
					}).done(function(data) {
						letfound(data);
					});

}

function scaleImageData(imageData, scale) {
  var scaled = ctx.createImageData(imageData.width * scale, imageData.height * scale);

  for(var row = 0; row < imageData.height; row++) {
    for(var col = 0; col < imageData.width; col++) {
      var sourcePixel = [
        imageData.data[(row * imageData.width + col) * 4 + 0],
        imageData.data[(row * imageData.width + col) * 4 + 1],
        imageData.data[(row * imageData.width + col) * 4 + 2],
        imageData.data[(row * imageData.width + col) * 4 + 3]
      ];
      for(var y = 0; y < scale; y++) {
        var destRow = row * scale + y;
        for(var x = 0; x < scale; x++) {
          var destCol = col * scale + x;
          for(var i = 0; i < 4; i++) {
            scaled.data[(destRow * scaled.width + destCol) * 4 + i] =
              sourcePixel[i];
          }
        }
      }
    }
  }

  return scaled;
}



function nn_resize(pixels,w1,h1,w2,h2) { 
	var temp = ctx.createImageData(w2, h2);
    var x_ratio = w1/w2;
    var y_ratio = h1/h2;
    var px = 0;
	var	py = 0; 
	
	for (var i=0; i<h2-1; i++) {
        py = Math.floor(i*y_ratio);
		for (var j=1; j<w2-1; j++) {
            px = Math.floor(j*x_ratio);
            //temp.data[(i*w2+j)*4 + 3] = pixels.data[(py*w1+px)*4 + 3]
			temp.data[(i*w2+j)*4 + 3] = (pixels.data[(py*w1+px)*4 + 3] + pixels.data[((py-1)*w1+px)*4 + 3] + pixels.data[((py+1)*w1+px)*4 + 3] + pixels.data[(py*w1+px-1)*4 + 3] + pixels.data[(py*w1+px+1)*4 + 3])
        }
    }
	return temp;
}
