#version 150
// GLSL version 1.50
// Fragment shader phong perPixel-highlights

#define MAXLIGHTS 5

// Variables passed in from the vertex shader
in float ndotl;
in vec4 frag_color;
in vec3 frag_normal;
in float frag_specularReflection; //the Reflection-Coefficient
in vec4 frag_cameraPosition; //the position of the camera, where do we look from
in float frag_phongEx; //the phongExponent
in mat4 frag_modelview;
in mat4 frag_cameraMatrix;
in float frag_lightIntensityArray[MAXLIGHTS];
in vec4 frag_lightPositionArray[MAXLIGHTS];

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	vec4 tempVec = frag_color;//vec4(0f,0f,0f,0f); //frag_color;initial lighting from Vertex-Shader
	
	for (int i = 0; i < MAXLIGHTS; i++){
		
		//reflect the lightsource
		vec4 lightDirection = normalize(gl_FragCoord-frag_lightPositionArray[i]);
		
		//reflect the light for shading, in CameraSpace
		vec4 lightReflectionA = normalize(reflect(lightDirection, frag_modelview*vec4(frag_normal,0)));
		
		//this is not integrated, slows to a crawl...
		float highlight_intensity = frag_lightIntensityArray[i]/dot(lightDirection,lightDirection); 
		
		//calculate specular-Moments: 
		//we calculate here: (R*e)^p with e = cameraPosition-gl_FragCoord
		float rDotE = max(normalize(dot(lightReflectionA, (frag_cameraPosition-gl_FragCoord))),0.0);
		
		float ndotAspec = frag_specularReflection * pow(rDotE,frag_phongEx);

		//sum it up
		tempVec += frag_color * ndotAspec; 
	}
	
	frag_shaded = tempVec;	
}
