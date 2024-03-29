console.log("this is script file");

const search=()=>{
	let query=$("#searchinput").val();
	
	
	if(query==''){
		$(".search-result").hide();
	}else{
		console.log(query);
		
		let url=`http://localhost:8383/search/${query}`;
		fetch(url).then((response)=>{
			return response.json();
		}).then((data)=>{
			console.log(data);
			
			let text= `<div class='list-group'>`;
			
			data.forEach((contact)=>{
				text+=`<a href="/user/contact/${contact.cid}" class='list-group-item list-group-item-action'> ${contact.name}</a>`
			});
			
			text+=`</div>`;
			
			
			$(".search-result").html(text);
			$(".search-result").show();
			
		});
		
	}
}