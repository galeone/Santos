/**
 * Helper functions for error pages
 */
var goBack = function() {
	history.back();
	return false;
}

var toggle = function(obj) {
	obj.style.display = obj.style.display == 'none' ? 'block' : 'none';
	return false;
}