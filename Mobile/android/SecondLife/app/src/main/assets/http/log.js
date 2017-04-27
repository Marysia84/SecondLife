var myVar=setInterval(function(){fetch_log()},2000);
var _apply_filter = false;
var _filter_token = '';
function clear_log_area()
{
	$( ".log_area" ).empty();
}

function apply_filter()
{
	_apply_filter = $("#apply_filter").prop('checked');
	_filter_token = $("#filter_token").val();
}

function fetch_log()
{
	$.get(	"/ajax", 
			{ next_index:""+next_index, arg1:"val1" }, 
				function(xml, status){
					$(xml).find('item').each(
						function(){
							var severity = $(this).find("severity").text();
							var text = $(this).find("text").text();
							var date_str = $(this).find("date").text();
							var date = new Date(date_str);
							var time_str = date.toLocaleTimeString();
							
							var append_text = true;
							if( true==_apply_filter )
							{
								if( -1==text.indexOf(_filter_token) )
								{
									append_text = false;
								}
							}
							
							if( true==append_text )
							{
								$( ".log_area" ).append( "<p class='"+severity+"'>["+severity+"] "+time_str+": "+text+"</p>" );
								$( ".log_area" ).scrollTop($(".log_area")[0].scrollHeight);
							}
						}
					);
					
					$(xml).find('index').each(
						function(){
							next_index = $(this).text();
						}
					);
				}, 
			"xml"
		);
}
function myStopFunction()
{
	clearInterval(myVar);
}