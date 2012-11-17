#version 150
// GLSL version 1.50
// Fragment shader phong perPixel-highlights

#define MAXLIGHTS 5

// Variables passed in from the vertex shader
in vec3 frag_normal;
in vec4 frag_position;
in float frag_specularReflection; //the Reflection-Coefficient
in vec4 frag_cameraPosition; //the position of the camera, where do we look from
in float frag_phongEx; //the phongExponent
in mat4 frag_modelview;
in float frag_lightIntensityArray[MAXLIGHTS];
in vec4 frag_lightPositionArray[MAXLIGHTS];
in vec4 frag_lightColorArray[MAXLIGHTS];
in float frag_reflectionCoefficient;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	vec4 tempVec = vec4(0.0,0.0,0.0,0.0);
	for (int i = 0; i < MAXLIGHTS; i++){
		
		//helpers	 	
	 	
	 	//light-Direction = light-position - pixel-position
	 	vec4 lightDirection = normalize(gl_FragCoord-frag_lightPositionArray[i]);
	 	
	 	//add damping square dot-product: multiplicate with itself, looks good, but doesnt's maybe need the sqrt
	 	float lightIntensity = frag_lightIntensityArray[i]/(dot(lightDirection,lightDirection));
		
		
		//diffuse point light 	
	 	float ndotA = max(0,dot(vec4(frag_normal,0), lightDirection));

	 	//specular
		//calculate specular-Moments: 
		//we calculate here: (R*e)^p with e = cameraPosition-gl_FragCoord
		float rDotE = max(0,normalize(dot(reflect(lightDirection, vec4(frag_normal,0)), (frag_cameraPosition-gl_FragCoord))));

	 	//put it all together
	 	ndotA = lightIntensity * ndotA * frag_reflectionCoefficient + frag_specularReflection * pow(rDotE,frag_phongEx);
		//float ndotAspec = frag_specularReflection * pow(rDotE,frag_phongEx);	 	
	 	
		//sum it up
		tempVec += frag_lightColorArray[i]*(ndotA); 	 	
		
	}

	frag_shaded = tempVec;	
}
