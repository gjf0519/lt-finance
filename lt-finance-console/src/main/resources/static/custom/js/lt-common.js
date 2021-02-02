function empty(v){
    switch (typeof v){
        case 'undefined' : return true;
        case 'string' : if(v.trim().length == 0) return true; break;
        case 'boolean' : if(!v) return true; break;
        case 'number' : if(0 === v) return true; break;
        case 'object' :
            if(null === v) return true;
            if(undefined !== v.length && v.length==0) return true;
            for(var k in v){return false;} return true;
            break;
    }
    return false;
}