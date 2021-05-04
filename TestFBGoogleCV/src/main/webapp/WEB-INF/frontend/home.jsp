<!DOCTYPE html>


<html>
<head>

<!-- Global site tag (gtag.js) - Google Analytics -->
<script async src="https://www.googletagmanager.com/gtag/js?id=G-7X5FRFYW67"></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());

  gtag('config', 'G-7X5FRFYW67');
</script>

<title>Facebook-GoogleCV</title>
<meta charset="UTF-8">

<link
	href="https://code.jquery.com/ui/1.10.4/themes/ui-lightness/jquery-ui.css"
	rel="stylesheet">
<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
<script src="https://code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
<script src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>



<style>
html, body {
	height: 100%;
	margin: 0;
}

.full-height {
	height: 100%;
	width: 100%;
	border-collapse: collapse;
}

.header {
  background-color:  #E5E7E9;
  padding: 30px;
  text-align: center;
  padding: 0px;
}

.sidebar {
  background-color: #f1f1f1;
  height: 100%;
  overflow: auto;
}

.content {
  height: 100%;
  width: 100%;
  overflow: auto;
}

.sidebar a {
  display: block;
  color: black;
  padding: 16px;
  text-decoration: none;
}
 
.sidebar a.active {
  background-color: #ff6699;
  color: white;
}

.sidebar a:hover:not(.active) {
  background-color: #0066ff;
  color: white;
}

.loader {
  border: 16px solid #f3f3f3;
  border-radius: 50%;
  border-top: 16px solid #3498db;
  width: 30px;
  height: 30px;
  -webkit-animation: spin 2s linear infinite; /* Safari */
  animation: spin 2s linear infinite;
}

/* Safari */
@-webkit-keyframes spin {
  0% { -webkit-transform: rotate(0deg); }
  100% { -webkit-transform: rotate(360deg); }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

</style>


<script>
	
	
	function fetchUserImage(){
		
		
		
		document.getElementById("loader").style.display = "block";
		
		$.ajax({
			  url: "/images",
			  type: "get", 
			  data: { 
			    
			    access_token:  document.getElementById("access_token").value,
			    user_id:  document.getElementById("user_id").value
			  },
			  success: function(response) {
			    console.log(response);
			    
			     createPhotoForApp(response);
			  },
			  error: function(xhr) {
				  document.getElementById("loader").style.display="none";
			    
			  }
			});
	}
	
	function cleanDashboard(){
		var content_div = document.getElementById("div_content");
		while (content_div.firstChild) {
			content_div.removeChild(content_div.lastChild);
		  }
		var sidebar_div = document.getElementById("div_sidebar");
		while (sidebar_div.firstChild) {
			sidebar_div.removeChild(sidebar_div.lastChild);
		  }
	}
	
	
	var currentLabelDisplay;
	var currentLabelA;
	
		function createPhotoForApp(response){
		var content_div = document.getElementById("div_content");
		var sidebar_div = document.getElementById("div_sidebar");
		
		content_div.appendChild(document.createElement("br"));
		content_div.appendChild(document.createElement("br"));
		 
		var isFirst=true;
		response.imageDataResponse.lables.forEach(label => {
		
		var label_div = document.createElement("div");
		label_div.setAttribute("id",label);
		
		var label_a = document.createElement("a");
		label_a.setAttribute("href","#"+label);
		label_a.setAttribute("id","a_"+label);	
		label_a.text=label;
		label_a.addEventListener("click", function(event) {
			var current_display_id = event.target.text;
			document.getElementById(current_display_id).style.display="block";
			//twttr.widgets.load();
			currentLabelDisplay.style.display="none";
			currentLabelDisplay = document.getElementById(current_display_id);
			
			event.target.style.backgroundColor= "#ff6699";
			event.target.style.color= "white";
			
			currentLabelA.style.backgroundColor= "#f1f1f1";
			currentLabelA.style.color= "black";
			
			currentLabelA = document.getElementById("a_"+current_display_id);
			
			});
		
		if(isFirst){
			label_div.style.display = "block";
			currentLabelDisplay=label_div;
			label_a.setAttribute("class","active");
			currentLabelA= label_a;
		}else{
			label_div.style.display = "none";
		}
		
		 content_div.appendChild(label_div);
		 
		 
		 sidebar_div.appendChild(label_a);
		 isFirst=false;
		 
		});
		 

		response.imageDataResponse.images.forEach(obj => {
	        
	      console.log(obj);
	      obj.labels.forEach(label => {
	    	  var label_div = document.getElementById(label);
	    	  label_div.appendChild(createImageElement(obj.url));
	    	  var p_space = document.createElement("p");
	    	  p_space.text="&#32";
	    	  label_div.appendChild(p_space);
	    	  label_div.appendChild(twiteer(obj.url));
	    	  label_div.appendChild(document.createElement("br"));
	    	  label_div.appendChild(document.createElement("br"));
	    	  label_div.appendChild(document.createElement("br"));
	    	  label_div.appendChild(document.createElement("br"));
	      })
	       
	    });
		
		
		document.getElementById("loader").style.display="none";
	}
	
	function changeDisplay(){
		alert(1)
	}
	
	function createImageElement(image_url){
		var img = document.createElement("img");
		img.setAttribute("src", image_url);
		img.style.height="275px";
		img.style.width="300px";
		return img;
	}
	
	function twiteer(image_url){
		var url = "https://twitter.com/intent/tweet?text=" + encodeURIComponent(image_url);
		var twitter = document.createElement('a');
		twitter.setAttribute('href', url);
		twitter.setAttribute('class', 'twitter-share-button');
		twitter.setAttribute("data-lang","en");
		twitter.innerHTML = "Tweet";
		return twitter;
	}
	
	
	
	
	

	
	  window.fbAsyncInit = function() {
	    FB.init({
	      appId      : '125356112918261',
	      cookie     : true,
	      xfbml      : true,
	      version    : 'v10.0'
	    });
	      
	    FB.AppEvents.logPageView();   
	      
	  };

	  (function(d, s, id){
	     var js, fjs = d.getElementsByTagName(s)[0];
	     if (d.getElementById(id)) {return;}
	     js = d.createElement(s); js.id = id;
	     js.src = "https://connect.facebook.net/en_US/sdk.js";
	     fjs.parentNode.insertBefore(js, fjs);
	   }(document, 'script', 'facebook-jssdk'));
	
	
	
	
	
	  function logout(){
		  FB.getLoginStatus(function(response){
		 	 FB.logout(function(response) {
		 		 console.log('Logged out');
		 		 window.location = "/";
		 		 
		 	});
		  });
		  }
	
	
	
	
	
</script>


</head>
<body onload="javascript:fetchUserImage()">
	<table class="full-height" >

		<tr style="height: 10%">
		
			<td style="align-items: center;" colspan="2">
				<div class="header" id="div_header">
				
					<table style="width: 100%; height: 100%; padding: 0px;">
					
						<tr>
						
							<td width="5%">
							
								
							</td>
							
							<td width="20%" align="left"><p style=" color: #0066ff"><%=request.getAttribute("user_name")%></td>
							<td width="45%" align="center"><p style="font-weight: bold;font-size: 25px; color: #ff0066">Facebook-GoogleCV App</p></td>
							
							<td><a id ="logout" href="#" align="right" onclick="logout()"> Log Out </a></td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
		<tr style="height: 85%">
			<td width="15%">
				<div class="sidebar" id="div_sidebar">

				</div>
			</td>
			<td width="85%" align="center">
				<div class="loader" id="loader" style="display:none"></div>
				<div id="div_content" class="content">
				</div>
			</td>
		</tr>
		
		
	</table>


	<form id="form_home" action="/home" method="post">
	 <input type="hidden" name="access_token" id="access_token" value="<%=request.getAttribute("access_token")%>">
	 <input type="hidden" name="user_name"  id="user_name"  value="<%=request.getAttribute("user_name")%>"> 
	  <input type="hidden" name="user_id"  id="user_id" value="<%=request.getAttribute("user_id")%>">
	</form>

		</body>
</html>