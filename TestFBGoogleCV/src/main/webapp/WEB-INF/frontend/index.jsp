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






<title>Facebook-GoogleCV App</title>
<meta charset="UTF-8">

<style>
html, body {
	height: 100%;
	margin: 0;
}

.full-height {
	height: 100%;
	width: 100%;
}
</style>

</head>
<body>
	<script>
		function statusChangeCallback(response) { 
			console.log('statusChangeCallback');
			console.log(response); 
			if (response.status === 'connected') { 
				document.getElementById("access_token").value = response.authResponse.accessToken;
				loadFormData();
			} else { 
				
				console.log("Login failed");
			}
		}

		function checkLoginState() { 
			FB.getLoginStatus(function(response) { 
				statusChangeCallback(response);
			});
		}
		
		
		  window.fbAsyncInit = function() {
		    FB.init({
		      appId      : '125356112918261',
		      cookie     : true,
		      xfbml      : true,
		      version    : 'v10.0'
		    });
		      
		    FB.AppEvents.logPageView();  
		    FB.getLoginStatus(function(response) { 
				statusChangeCallback(response);
			});
		      
		  };

		  (function(d, s, id){
		     var js, fjs = d.getElementsByTagName(s)[0];
		     if (d.getElementById(id)) {return;}
		     js = d.createElement(s); js.id = id;
		     js.src = "https://connect.facebook.net/en_US/sdk.js";
		     fjs.parentNode.insertBefore(js, fjs);
		   }(document, 'script', 'facebook-jssdk'));
		

		function loadFormData() { 
			console.log('Welcome! Plese wait... Getting your information.... ');
			FB
					.api(
							'/me?fields=name',
							function(response) {
								console.log('Successful login for: ' + response.name);
								console.log(response);
								document.getElementById('user_id').value = response.id;
								document.getElementById('user_name').value = response.name;
								document.getElementById("form_home").submit();
								
							});
		}
		

		function logout(){
		 	 FB.logout(function(response) {
		 		 console.log('Logged out')
		 	});
		  }
		
	</script>


	<div id="fb-root"></div>
	



	<table class="full-height">

		<tr style="height: 5%">
			<td style="align-items: center;">
				<div id="status"></div>
			</td>
		</tr>
		<tr style="height: 20%">
			<td align="center">
				<div id=""><p style="font-weight: bold;font-size: 50px; color: #ff0066"> Facebook-GoogleCV App </p></div>
			</td>
		</tr>
		<tr style="height: 30%">

			<td align="center"><fb:login-button scope="user_photos"
					onlogin="checkLoginState();" size="xlarge">Login with Facebook</fb:login-button>
			</td>

		</tr>

		<tr style="height: 20%">
			<td align="center" >
				<div id=""><p style="font-size: 30Px; color: #f27c05;"> Explore Google CV on your FB photos </p></div>
			</td>
		</tr>
		
	</table>


	<form id="form_home" action="/home" method="post">
	 <input type="hidden" name="access_token" id="access_token">
	 <input type="hidden" name="user_name"  id="user_name"> 
	  <input type="hidden" name="user_id"  id="user_id">
	</form>

	<!-- Load the JS SDK asynchronously -->
			<script async defer crossorigin="anonymous"
				src="https://connect.facebook.net/en_US/sdk.js"></script>

		</body>
</html>