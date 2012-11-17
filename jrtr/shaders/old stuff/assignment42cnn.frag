#version 150
// GLSL version 1.50
// Fragment shader phong perPixel-highlights

#define MAXLIGHTS 5
uniform vec4 lightPositionArray[MAXLIGHTS]; //sh the following should create a vec4 array
uniform float lightIntensityArray[MAXLIGHTS]; //sh light intensities
uniform vec4 lightColorArray[MAXLIGHTS]; //sh light colors
uniform float reflectionCoefficient; //sh shininness
uniform float phongExponent; //sh Phong-Exponent
uniform float specularReflection; //sh specularReflection
uniform vec4 cameraPosition; //sh camera position


// Variables passed in from the vertex shader
in vec3 frag_normal;
in vec4 frag_position;
in mat4 frag_modelview;

in vec4 frag_cameraPosition; //the position of the camera, where do we look from




// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	
	vec4 tempVec = vec4(0.0,0.0,0.0,0.0);
	vec4 highlightColor	= vec4(1.0,1.0,1.0,1.0);
	for (int i = 0; i < MAXLIGHTS; i++){

	 	//light-Direction = light-position - pixel-position
	 	vec4 lightDirection = normalize(lightPositionArray[i]-frag_modelview*frag_position);//frag_modelview*frag_position); //gl_FragCoord
	 	
	 	//lightDistance: (sqrt(x^2+y^2+z^2)) 
	 	float distance = length(frag_position-lightPositionArray[i]);//gl_FragCoord
	 	
	 	//add damping distance^2
	 	float lightIntensity = lightIntensityArray[i]/pow(distance,2);
		
		//diffuse point light 	
	 	float ndotA = max(0,dot(vec4(frag_normal,0), lightDirection)); //maybe give this in!!!!

	 	//specular
		//calculate specular-Moments: 
		//we calculate here: (R*e)^p with e = cameraPosition-gl_FragCoord
		vec4 R = reflect(-lightDirection, vec4(frag_normal,0));
		vec4 e = normalize(frag_position-cameraPosition);
		float rDotE = max(0,dot(R,e)); //maybe doesn't need max(0)

	 	//put it all together
	 	ndotA = lightIntensity *(ndotA * reflectionCoefficient);
	 	float ndotAspec = lightIntensity*(specularReflection * pow(rDotE,phongExponent));	 	
	 	
		//sum it up
		//tempVec += lightColorArray[i]*(ndotA);
		tempVec += highlightColor*ndotAspec; 	 	
		
	}

	frag_shaded = tempVec;	
}
