(function($){

	$.fn.selectList = function(optinons){
		
		var opts = $.extend({},$.fn.selectList.defluts,optinons);

		var $this = $(this),
			wrapper = "<div style='position:relative;display:inline-block;zoom:1;*display:inline;'></div>",
			inputVal = "<div class='selectdown' style='border: 1px solid #C9C9C9;border-radius: 3px;padding:3px 15px 3px 5px;cursor:pointer'></div>",
			dropList = "<ul class='_list'></ul>",
			thisW = $this.width();

			$this.hide().wrap(wrapper);
			$this.after(dropList).after(inputVal);

		var selectedText = $this.children('option:selected').text();

		$this.siblings('div').html(selectedText);

		for(var i=0; i<$this.children('option').length; i++){
			//alert($this.children('option').eq(i).text());
			$("<li>"+$this.children('option').eq(i).text()+"</li>").appendTo($this.siblings('ul'));
		}
		$this.siblings('div').css({
			'width': thisW + 'px'
		})

		var inputValH = $this.siblings('div').innerHeight();
		var inputValW = $this.siblings('div').innerWidth();
		var ul = $this.siblings('ul');
		var selcIndex = $this.children('option:selected').index();
		var open = false;
		
		ul.hide().css({
			'top' : (inputValH) + 'px',
			'left' : '0px',
			'width' : inputValW + 'px',
			'position': 'absolute',
			'background': '#fff',
			'border': '1px solid #C9C9C9'
		})
		ul.children('li').css({
			'line-height': '24px',
			'border-top': '1px solid #C9C9C9',
			'padding': '0 5px',
			'cursor':'pointer'
		}).eq(selcIndex).addClass('cur');
		ul.children('li').first().css({
			'border-top': 'none'
		});

		$this.siblings('div').bind('click',function(event){
			if(open == false){
				$(this).addClass('selectup').parents('div').css('z-index','99');
				$(this).css({
				'border':'1px solid #C9C9C9'
				});
				ul.show();
				open = true;
			}else{
				$(this).parents('div').css('z-index','1');
				$(this).removeClass('selectup').addClass('selectdown').css({
				'border':'1px solid #C9C9C9'
				});
				ul.hide();
				open = false;
			}
			event.stopPropagation();
			
		});

		ul.children('li').bind('click',function(){
			ul.hide().children('li').removeClass('cur');
			$(this).addClass('cur');
			$this.siblings('div').html($(this).text());
			$this.children('option').removeAttr('selected').eq($(this).index()).attr('selected','selected');
			$this.siblings('div').removeClass('selectup').addClass('selectdown').css({
				'border':'1px solid #C9C9C9'
				});
			open = false;
		});

		$(document).click(function(){
			ul.hide();
			$this.parents('div').css('z-index','1');
			$this.siblings('div').removeClass('selectup').addClass('selectdown').css({
				'border':'1px solid #C9C9C9'
				});
			open = false;
		});

	}
	

})(jQuery);