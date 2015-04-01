function dataTablesCheckbox( data, type, full, meta ) {
    return data === true || data === 'true' ? "Si" : "No";
};

function dataTablesColor( data, type, full, meta ) {
	return '<div style="background-color:'+data+';color:#000">'+data+'</div>';
};