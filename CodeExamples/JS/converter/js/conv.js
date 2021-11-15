function split(){
    var full_value=Number(document.getElementsByClassName("full_input")[0].value);
    if(isNaN(full_value)){
        alert("Вы ввели некорректное значение. Величина может принимать значение в диапазоне от 0 до 65535.");
        document.getElementsByClassName("full_input")[0].value='';
        return;
    }
    if(full_value<0){
        alert("Величина не может быть отрицательной. Величина может принимать значение в диапазоне от 0 до 65535.");
        document.getElementsByClassName("full_input")[0].value='';
        return;
    }
    if(full_value>65535){
        alert("Вы ввели слишком большое значение. Величина может принимать значение в диапазоне от 0 до 65535.");
        document.getElementsByClassName("full_input")[0].value='';
        return;
    }
    full_value=full_value.toString(16)
    var leng=full_value.length;
    if(leng==3){
        full_value='0'+full_value;
    }else if(leng==2){
        full_value='00'+full_value;
    }else if(leng==1){
        full_value='000'+full_value;
    }
    var high_part=full_value.slice(0,2);
    var low_part=full_value.slice(2,4);
    high_part=''+parseInt(high_part,16);
    low_part=''+parseInt(low_part,16);

    if(low_part.length==1){
        low_part='00'+low_part;
    }else if(low_part.length==2){
        low_part='0'+low_part;
    }

    if(high_part.length==1){
        high_part='00'+high_part;
    }else if(high_part.length==2){
        high_part='0'+high_part;
    }
    document.getElementsByClassName("splitted_high")[0].value=high_part;
    document.getElementsByClassName("splitted_low")[0].value=low_part;
};

function merge(){
    var high_value=Number(document.getElementsByClassName("splitted_high")[0].value);
    var low_value=Number(document.getElementsByClassName("splitted_low")[0].value);
    if(isNaN(high_value)){
        alert("Вы ввели некорректное значение. Величина может принимать значение в диапазоне от 0 до 255.");
        document.getElementsByClassName("splitted_high")[0].value='';
        return;
    }
    if(high_value<0){
        alert("Величина не может быть отрицательной. Величина может принимать значение в диапазоне от 0 до 255.");
        document.getElementsByClassName("splitted_high")[0].value='';
        return;
    }
    
    if(high_value>255){
        alert("Величина не может быть больше 255.");
        document.getElementsByClassName("splitted_high")[0].value='';
        return;
    }
    
    if(isNaN(low_value)){
        alert("Вы ввели некорректное значение. Величина может принимать значение в диапазоне от 0 до 255.");
        document.getElementsByClassName("splitted_low")[0].value='';
        return;
    }
    if(low_value<0){
        alert("Величина не может быть отрицательной. Величина может принимать значение в диапазоне от 0 до 255.");
        document.getElementsByClassName("splitted_low")[0].value='';
        return;
    }
    
    if(low_value>255){
        alert("Величина не может быть больше 255.");
        document.getElementsByClassName("splitted_low")[0].value='';
        return;
    }

    high_value=high_value.toString(16);
    low_value=low_value.toString(16);
    if(low_value.length==1){
        low_value='0'+low_value;
    }
    if(high_value.length==1){
        high_value='0'+high_value;
    }
    if(high_value.length!=2 && low_value.length!=2){
        alert("Ошибка преобразования. Проверьте корректность данных.");
        return;
    }

    var result_string=''+high_value+low_value;
    document.getElementsByClassName("full_input")[0].value=parseInt(result_string,16);
}