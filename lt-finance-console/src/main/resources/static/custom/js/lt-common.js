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

//四舍五入保留2位小数（不够位数，则用0替补）
function keepTwoDecimalFull(num) {
    var result = parseFloat(num);
    if (isNaN(result)) {
        alert('传递参数错误，请检查！');
        return false;
    }
    result = Math.round(num * 100) / 100;
    var s_x = result.toString(); //将数字转换为字符串
    var pos_decimal = s_x.indexOf('.'); //小数点的索引值
    // 当整数时，pos_decimal=-1 自动补0
    if (pos_decimal < 0) {
        pos_decimal = s_x.length;
        s_x += '.';
    }
    // 当数字的长度< 小数点索引+2时，补0
    while (s_x.length <= pos_decimal + 2) {
        s_x += '0';
    }
    return s_x;
}