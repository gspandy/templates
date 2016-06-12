$( document ).ready(function() {
	
	
	 var getfilter="'|(and|or)\\b.+?(>|<|=|in|like)|\\/\\*.+?\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT|UPDATE.+?SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE).+?FROM|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)";
	 
     var postfilter= new RegExp("\\b(and|or)\\b.{1,6}?(=|>|<|\\bin\\b|\\blike\\b)|\\/\\*.+?\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT|UPDATE.+?SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE).+?FROM|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)",'g');
     
     
     var filterData = function(data){
    	 for(var i=0;i<data.length;i++){
    		 data[i].value = data[i].value.replace(postfilter,"");
    	 }
     }
     
	$(".parameter_input").change(function(){
		var target = $(this).attr("target");
		var format = $(this).find("option:selected").attr("format");
		$(target).html(format);
	});
	
	
	$("#config_form").bind("submit", function() {
		var groupService = $(this).find(".group-service").val();
		var splits = groupService.split("/");
		if(splits.length!=2){
			alert("分组/服务输入格式错误")
			return false;
		}
		
		var value = $(this).find("[name=value]").val();
		if(jQuery.trim(value)==""){
			alert("请输入参数值");
			return false;
		}
			

		
		$(this).find(".group").val(splits[0]);
		$(this).find(".service").val(splits[1]);
		
		var data = $(this).serializeArray()
		filterData(data);
	    $.ajax({
	            type     : "POST",
	            cache    : false,
	            url      : $(this).attr('action'),
	            data     : data,
	            success  : function(data) {
	                var  ret = data.ret;
	                if(ret==200){
	                	$("#newConfigModal").modal("hide");
	                	location.reload();
	                }
	                else{
	                	alert("保存错误:"+data.msg)
	                }
	            }
	    });
	    return false;
	});
//	
	$("#config_modify_form").bind("submit", function() {
		
		var data = $(this).serializeArray();
		
		filterData(data);
		
	    $.ajax({
	            type     : "POST",
	            cache    : false,
	            url      : $(this).attr('action'),
	            data     : data,
	            success  : function(data) {
	                var  ret = data.ret;
	                if(ret==200){
	                	$("#modifyConfigModal").modal("hide");
	                	location.reload();
	                }
	                else{
	                	alert("保存错误:"+data.msg)
	                }
	            }
	    });
	    return false;
	});
//	
//	
	$(".modify").click(function(){
		var id = $(this).data("id");
		var group = $(this).data("group");
		var service = $(this).data("service");
		var method = $(this).data("method");
		var parameter = $(this).data("parameter");
		var value = $(this).data("value");
		var modifyConfigModal = $("#modifyConfigModal")
		modifyConfigModal.find("[name=id]").val(id);
		modifyConfigModal.find("[name=group]").val(group);
		modifyConfigModal.find("[name=group-service]").val(group+"/"+service);
		modifyConfigModal.find("[name=service]").val(service);
		modifyConfigModal.find("[name=method]").val(method);
		modifyConfigModal.find("[name=parameter]").val(parameter);
		modifyConfigModal.find("[name=value]").val(value);
		modifyConfigModal.modal("show");
		
		

	});
//	
//	
	$("#select-all").change(function(){
		var selectConfigs = $(".config-select");

		selectConfigs.prop("checked",$(this).prop("checked"));

	});
//	
	$("#delete-config-btn").click(function(){
		var selectConfigs = $(".config-select:checked");
		var ids = [];
		var selectConfigTD = [];
		for(var i=0;i<selectConfigs.length;i++){
			ids.push($(selectConfigs[i]).data("id"));
			selectConfigTD.push($(selectConfigs[i]).parent().parent());
		}
		var deleteUrl = $(this).attr("delete-url");
		
        $.ajax({
            type: "POST",
            url: deleteUrl,
            data: JSON.stringify(ids),
            contentType: "application/json;charset=UTF-8",
            success: function (body) {
                var result = JSON.parse(body);
                if(result.ret==200){
                    alert("成功")
                    for(var i=0;i<selectConfigTD.length;i++){
                    	selectConfigTD[i].remove();
                    }
                }
                else
                    alert("失败：" +result.msg);
            },
            dataType: "text"
        })
	})
//	
//    $( "#group_and_environment option:selected" ).each(function() {
//    	var app = $(this).attr("app");
//    	if(app!=undefined){
//    		$("#system_name").val(app);
//    	}
//    });
//	
//	
//    $( "#group_and_environment" ).change(function() {
//    	 var app = $(this).find("option:selected").attr("app");
//     	if(app!=undefined){
//    		$("#system_name").val(app);
//    	}
//    });
});