#version 150
// GLSL version 1.50
// Fragment shader for diffuse shading in combination with a texture map

// Uniform variables passed in from host program
uniform sampler2D myTexture;

// Variables passed in from the vertex shader
in float ndotl;
in vec4 frag_color;
in vec3 frag_normal;
in float frag_specularReflection; //the Reflection-Coefficient
in vec4 frag_cameraPosition; //the position of the camera
in vec4 lightReflectionA; //the reflected light-vector
in vec4 lightReflectionB; //the reflected light-vector
in float frag_phongEx; //the phongExponent

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		

	//calculate specular-Moments: 
	
	float ndotBspec = frag_specularReflection * pow(dot(lightReflectionA, frag_cameraPosition),frag_phongEx); //this is wrong :)
	
	
	frag_shaded = frag_color * (ndotl + ndotBspec);
	
	
		
}
