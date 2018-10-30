$(document).ready(function(){

	$("#emailMe").click(function(){
		var data = [];
		//alert("Email sent!");
		$("input[name='productId']").each(function() {
			if(this.checked) {
				console.log( this.value + ":" + this.checked );
				data.push(this.value);
				this.checked = false;
			}
		});
		$.ajax({
				type: "POST",
				url: "/product/email",
				data:  JSON.stringify(data),
				contentType: 'application/json'
			})
			.done(function(response) {
				alert("Server replied: " + response );
			})
			.fail(function(response) {
			   alert("Failed. Server replied:" + response);
			 });
	});
});


function markForDelete(id) {
	$.ajax({
        type: "POST",
        url: "/product/markForDelete",
        data:  id,
        contentType: 'application/json'
    })
    .done(function(response) {
        $('#product_' + id).html('<td colspan="6" class="tr-deleted">Product <a target="_blank" href="/product/get/' + id + '">'+ id +'</a> has been marked for deletion</td>');
        //alert("Server replied: " + response );
    })
    .fail(function(response) {
       alert("Failed. Server replied:" + response);
     });
}
