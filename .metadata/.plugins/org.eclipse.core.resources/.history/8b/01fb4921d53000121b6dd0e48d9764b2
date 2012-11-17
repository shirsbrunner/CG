#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture map

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*
uniform mat4 projection;
uniform mat4 modelview;
uniform vec4 lightDirection;

const int maxLights = 5; //maybe set this final

uniform vec4 lightDirectionArray[maxLights]; //sh the following should create a vec4 array
uniform float lightIntensityArray[maxLights]; //sh light intensities
uniform float reflectionCoefficient; //sh shininness
uniform float phongExponent; //sh Phong-Exponent
uniform float specularReflection; //sh specularReflection
uniform vec4 cameraPosition; //sh camera position

//this sets a default-Color for the non texture shaders
uniform vec4 color = vec4(1.0, 1.0, 0.0, 0.0);

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec4 position;
in vec2 texcoord;

// Output variables for fragment shader
out vec3 frag_normal; //the normal
out float ndotl; //the diffuse light
out vec4 frag_color; //the color of the Object
out float frag_specularReflection; //the Reflection-Coefficient
out vec4 frag_cameraPosition; //the position of the camera
out vec4 lightReflectionA; //the reflected light-vector
out vec4 lightReflectionB; //the reflected light-vector
out float frag_phongEx; //the phongExponent
out vec2 frag_texcoord;

void main()
{		
	frag_color = color;
	frag_phongEx = phongExponent;
	frag_specularReflection = specularReflection;
	frag_cameraPosition = cameraPosition;
	frag_texcoord = texcoord;
	
	lightReflectionA = reflect(lightDirectionArray[0] , modelview * vec4(normal,0)); //reflect the light for shading, in CameraSpace
	lightReflectionB = reflect(lightDirectionArray[1] , modelview * vec4(normal,0)); //reflect the light for shading, in CameraSpace
	
	//transform normal to cameraSpace
	frag_normal = (modelview * vec4(normal,0)).xyz; //this should take the 3 first values of the 4f Vector 
	
		
	// Compute dot product of normal and light direction
	// and pass color to fragment shader
	// Note: here we assume "lightDirection" is specified in camera coordinates,
	// so we transform the normal to camera coordinates, and we don't transform
	// the light direction, i.e., it stays in camera coordinates
	// tried a loop the result is a black screen, compiler issue?
	
	//TODO calculate e = cameraPosition - 
	float ndotB = reflectionCoefficient * max(dot(modelview * vec4(normal,0), lightDirectionArray[0]),0); //sh changed this
	float ndotA = reflectionCoefficient * max(dot(modelview * vec4(normal,0), lightDirectionArray[1]),0); //sh changed this
	
	ndotB = ndotB * lightIntensityArray[0];
	ndotA = ndotA * lightIntensityArray[1];
	
	ndotl = ndotA + ndotB; //sum two lightsources

	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	gl_Position = projection * modelview * position;
}
