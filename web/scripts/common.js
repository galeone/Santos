function dataTablesCheckbox( data, type, full, meta ) {
    return data === true || data === 'true' ? "Si" : "No";
};

function dataTablesColor( data, type, full, meta ) {
	return '<div style="background-color:'+data+';color:#000">'+data+'</div>';
};

function dataTablesLeadTime( data, type, full, meta ) {
	var leadTime    = parseInt(data),
		hours       = leadTime % 24,
		days        = Math.floor(leadTime / 24),
		daysString  = days === 0  ? "" : days  === 1 ? "1 giorno" : days  + " giorni",
		hoursString = hours === 0 ? "" : hours === 1 ? "1 ora"    : hours + " ore";
	return daysString + (daysString !== "" && hoursString !== "" ? " e " : "") + hoursString;
};