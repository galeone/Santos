function dataTablesCheckbox( data, type, full, meta ) {
    return data === true || data === 'true' ? "Si" : "No";
};

function dataTablesColor( data, type, full, meta ) {
	return '<div style="background-color:'+data+';color:#000">'+data+'</div>';
};

function dataTablesLeadTime( data, type, full, meta ) {
	var leadTime        = parseInt(data),
		minutes	    = leadTime % 60,
		hours       = Math.floor(leadTime / 60),
		days        = Math.floor(hours / 24),
		hours	    = hours - 24*days,
		daysString  = days === 0  ? "" : days  === 1 ? "1 giorno" : days  + " giorni",
		hoursString = hours === 0 ? "" : hours === 1 ? "1 ora"    : hours + " ore",
		minutesString = minutes === 0 ? "" : minutes === 1 ? "1 minuto"    : minutes + " minuti";

		ret = isNaN(leadTime) ? data : daysString + (daysString !== "" && hoursString !== "" ? " e " : "") + hoursString +
			(minutesString !== "" && (daysString !== "" || hoursString !== "") ? " e " : "") + minutesString;
	return ret === "" ? "Ore completamente assegnate" : ret;
};

function createDateAsUTC(date) {
    return new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds()));
};

function convertDateToUTC(date) { 
    return new Date(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds()); 
};


$.editable.addInputType('leadtime', {
	  element: function(settings, original) {
	    $(this).append('<label>Ore <input type="number" name="ore" class="ore" /></label>' +
	    		'<label>Minuti<input type="number" name="minuti" class="minuti" /></label>');
	    var hidden = $('<input type="hidden" value="0"/>');
	    $(this).append(hidden);
	    return(hidden);
	  },

	  submit: function(settings, original) {
		var hours = parseInt($(".ore", this).val()),
			minutes =  parseInt($(".minuti", this).val());
		minutes  = isNaN(minutes)  ? 0 : minutes;
		hours    = isNaN(hours)    ? 0 : hours;
		
		$(':hidden', this).val(hours * 60 + minutes);
	  }
});
