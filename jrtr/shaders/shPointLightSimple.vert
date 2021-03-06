#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture map

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*
uniform mat4 projection;
uniform mat4 modelview;
uniform mat4 cameraMatrix;

#define MAXLIGHTS 5 //maybe set this final


uniform vec4 lightPositionArray[MAXLIGHTS]; //sh the following should create a vec4 array
uniform float lightIntensityArray[MAXLIGHTS]; //sh light intensities
uniform float reflectionCoefficient; //sh shininness
uniform mat4 transformation; //sh transformation of the object

uniform vec4 color = vec4(1.0, 0.0, 0.0, 0.0); //this could be in as well...

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec4 position;
in vec2 texcoord;

// Output variables for fragment shader
out float ndotl;
out vec4 frag_color;

void main()
{		
	frag_color = color;
		
	// Compute dot product of normal and light direction
	// if it's not an ambient light, the distance matters and the angle matters

	ndotl = 0;
	for (int i = 0; i < MAXLIGHTS; i++){
	 	
	 	//light-Direction = light-position - pixel-position
	 	vec3 lightDirection = (cameraMatrix*vec4(lightPositionArray[i].xyz,1) - modelview*position).xyz;
	 	float ndotA = max(0,dot((modelview * vec4(normal,0)).xyz, normalize(lightDirection)));
	 	
	 	//add damping square(sqrt dot-product)
	 	float lightIntensity = lightIntensityArray[i]/(dot(lightDirection,lightDirection));
	 	
	 	//put it all together
	 	ndotA = ndotA * reflectionCoefficient * lightIntensity;
	 	
	 	//sum lightsources
	 	ndotl += ndotA;
	}
	
	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	gl_Position = projection * modelview * position;
}
