class Rule(object):
    
    def __init__(self):

        self._clauses = {}
        self._numClauses = 0
        
    def setTarget(self, targetName, targetValue):           
        self._targetName = targetName
        self._targetValue = targetValue

    def addClause(self, attName, attValue):
        
        self._clauses[attName] = attValue
        self._numClauses += 1 
    
    def prune(self, dataset, parameters):
        
        clauses = { key: value for key, value in self._clauses.items() if key != "root" }
        names = map(lambda x : x.getName(), parameters)
        att_idx = {x:names.index(x) for x in clauses.keys()}
        
        match = 0.0
        for sample in dataset:
            check = True
            for att in clauses.keys():
                check = check and sample[att_idx[att]] == clauses[att]
            if check:
                match += 1.0            
        if match/len(dataset) < 0.07:
            return EmptyRule()
        
        baseline = self._getAccuracy(dataset, parameters)
        
        
        for omitted in clauses:
            error = self._getAccuracy(dataset, parameters, omitted)
            
            if abs(error - baseline) < 0.03:    
                self._clauses.pop(omitted)
                self._numClauses -= 1
            
        return self
        
    def _getAccuracy(self, dataset, parameters, *omitted):
        params = dict(self._clauses)
        
        if len(omitted) == 1:
            params.pop(omitted[0])
        
        #Create a map containing the indices of the attributes in the samples
        #do not print the root element
        clauses = { key: value for key, value in params.items() if key != "root" }
        names = map(lambda x : x.getName(), parameters)
        att_idx = {x:names.index(x) for x in clauses.keys()}
        
        error = .0
        correct = 0.0
        
        for sample in dataset:
            check = True
            for att in clauses.keys():
                check = check and sample[att_idx[att]] == clauses[att]
            
            if check and sample[0] != self._targetValue:
                error += 1
               
        #the less samples we cover the bigger the error
        return error/len(dataset)           
       
    def __str__(self):
        strRep = self._targetName + ' = ' + self._targetValue + ' <- '
        amps = ['.'] + [' & '] * (self._numClauses - 2)
        
        #do not print the root element
        clauses = { key: value for key, value in self._clauses.items() if key != "root" }
        
        for clause in clauses:
            strRep = strRep + clause + ' = ' + self._clauses[clause] + amps.pop() 
        return strRep
    
class EmptyRule(Rule):
        def __str__(self):
            return 'emptyRule'