/**
 * Usage:
 * 
 * 1. Install Jeditable: http://www.appelsiini.net/projects/jeditable
 * 2. Install farbtastic: https://github.com/mattfarina/farbtastic
 * 3. Add the code below to your javascript.
 * 4. Call it like this:
 * 
 * $('p').editable('/edit', {
 *     type: 'farbtastic',
 *     cancel: 'Cancel'
 *     submit: 'OK'
 * });
 * 
 * Upon clicking on the <p>, it's content will be replaced by a farbtastic colorpicker.
 * 
 * Paolo Galeone (nessuno@nerdz.eu)
 */

$.editable.addInputType('farbtastic', {
  element: function(settings, original) {
	  $(this).append('<div class="picker"></div>');
	  var hidden = $('<input type="hidden" />');
	  $(this).append(hidden);
	  return hidden;
  },

  content: function(data, settings, original) {
	  settings = $.extend({ farbtastic: {} }, settings);
	  var $hidden = $('input', this), $picker = $('.picker', this);
	  $.farbtastic($picker).linkTo(function(color) {
		  $hidden.val(color);
	  });
  }
});
