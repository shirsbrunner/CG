#version 150
// GLSL version 1.50
// Fragment shader phong perPixel-highlights

#define MAXLIGHTS 5
uniform vec4 lightColorArray[MAXLIGHTS]; //sh light colors
uniform float reflectionCoefficient; //sh shininness
uniform float phongExponent; //sh Phong-Exponent
uniform float specularReflection; //sh specularReflection

uniform float lightIntensityArray[MAXLIGHTS]; //sh light intensities


// Variables passed in from the vertex shader
in vec3 frag_normal;
in vec4 frag_position;
in mat4 frag_modelview;
in vec4 lightDirectionArray[MAXLIGHTS];
in float frag_lightIntensityArray[MAXLIGHTS];
in float frag_distanceArray[MAXLIGHTS];
in vec4 frag_e; //e for phong shader

in vec4 frag_cameraPosition; //the position of the camera, where do we look from




// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	
	vec4 tempVec = vec4(0.0,0.0,0.0,0.0);
	vec4 highlightColor	= vec4(1.0,1.0,1.0,1.0);
	for (int i = 0; i < MAXLIGHTS; i++){

	 	//light-Direction = light-position - pixel-position
	 	vec4 lightDirection = lightDirectionArray[i];
	 			
		float lightIntensity = lightIntensityArray[i]/(pow(frag_distanceArray[i],2));

		//diffuse point light 	
	 	float ndotA = max(0,dot(vec4(frag_normal,0), lightDirection));

	 	//specular
		//calculate specular-Moments: 
		//we calculate here: (R*e)^p with e = cameraPosition-gl_FragCoord
		vec4 R = reflect(lightDirection, vec4(frag_normal,0));

		float rDotE = max(0,dot(R,frag_e)); //maybe doesn't need max(0)

	 	//put it all together
	 	ndotA = lightIntensity *(ndotA * reflectionCoefficient);
	 	float ndotAspec = lightIntensity*(specularReflection * pow(rDotE,phongExponent));	 	
	 	
		//sum it up
		tempVec += lightColorArray[i]*(ndotA);
		tempVec += highlightColor*ndotAspec; 	 	
		
	}

	frag_shaded = tempVec;	
}
