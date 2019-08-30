<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Yigo 全文检索系统</title>

	<script type="text/javascript" src="js/jquery-1.10.2.js"></script>
	
	<script type="text/javascript">
	
		$(function(){
			
			
			var dtd = $.Deferred(); // 新建一个deferred对象
			var wait = function(dtd){
			    var tasks = function(){
			        alert("执行完毕！");
			        dtd.resolve(); // 改变deferred对象的执行状态
			    };
			    setTimeout(tasks,5000);
			    return dtd;
			};
			//$.when()的参数只能是deferred对象
			// 如果传入普通对象,绑定的回调函数将会立即执行
			//$.when(wait(dtd))
			
			console.log($.when(wait));
			
			wait(dtd).done(function(){ alert("哈哈，成功了！"); })
			.fail(function(){ alert("出错啦！"); });
			
			
			
			
			
			$("#btn").click(function(){
								
				var keyword = $("#keyword").val();
				
				 $.post('/docsearcher/search',{keyword:keyword},
					  	function(data){
							
					 		if( !data )
					 			return;
					 							 		
					 		var $div = $("#result");
					 		
					 		$div.attr("enable",false);
					 		
					 		alert(typeof $div.attr("enable"));
					 		
					 		var array = JSON.parse(data);
					 	
					 		var item;
					 		for( var i = 0,size = array.length;i < size;i++ ) {
								item = array[i];
					 			var $a = $('<a/>');
								$a.html(item.title);
								$a.attr('href',"/docsearcher/files/Yigo1.html").attr('target','_blank');
								$div.append($a).append($('<br/>'));
					 		} 
		    			}
				 );
				
			});
			 			  
		});			
	
	
	
	</script>

</head>
<body>
	<form action="/docsearcher/search" method="GET">
		关键字: <input type="text" id="keyword" name="keyword"><br/>
		<input type="button" id="btn" value="查询" />
	</form>
	<div id="result"></div>
</body>
</html>