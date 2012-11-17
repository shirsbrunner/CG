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

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec4 position;
in vec2 texcoord;

// Output variables for fragment shader
out float ndotl;
out vec2 frag_texcoord;

void main()
{		
	frag_texcoord = texcoord;
		
	// Compute dot product of normal and light direction
	// if it's not an ambient light, the distance matters and the angle matters

	ndotl = 0;
	for (int i = 0; i < MAXLIGHTS; i++){
	 	
	 	//light-Direction = light-position - pixel-position
	 	vec3 lightDirection = (cameraMatrix*vec4(lightPositionArray[i].xyz,1) - modelview*position).xyz;
	 	float ndotA = max(0,dot((modelview * vec4(normal,0)).xyz, normalize(lightDirection)));
	 	//add damping square dot-product: multiplicate with itself, looks good, but doesnt's maybe need the sqrt
	 	float lightIntensity = lightIntensityArray[i]/sqrt(dot(lightDirection,lightDirection));
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
