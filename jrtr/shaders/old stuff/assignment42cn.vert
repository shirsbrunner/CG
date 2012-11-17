#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture map

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*
uniform mat4 projection;
uniform mat4 modelview;

uniform vec4 lightDirection; //the lightDirection
#define MAXLIGHTS 5
uniform vec4 lightPositionArray[MAXLIGHTS]; //sh the following should create a vec4 array
uniform float lightIntensityArray[MAXLIGHTS]; //sh light intensities
uniform vec4 lightColorArray[MAXLIGHTS]; //sh light colors
uniform float reflectionCoefficient; //sh shininness
uniform float phongExponent; //sh Phong-Exponent
uniform float specularReflection; //sh specularReflection
uniform vec4 cameraPosition; //sh camera position

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec4 position;
in vec2 texcoord;

// Output variables for fragment shader
out vec3 frag_normal; //the normal
//out vec4 frag_color; //the color of the Object
out float frag_specularReflection; //the Reflection-Coefficient
out vec4 frag_cameraPosition; //the position of the camera
out float frag_phongEx; //the phongExponent
out vec2 frag_texcoord;
out mat4 frag_modelview; //modelview & projection
out vec4 frag_position;
out float frag_lightIntensityArray[MAXLIGHTS];
out vec4 frag_lightPositionArray[MAXLIGHTS];
out vec4 frag_lightColorArray[MAXLIGHTS];
out float frag_reflectionCoefficient;


void main()
{		
	frag_phongEx = phongExponent;
	frag_specularReflection = specularReflection;
	frag_cameraPosition = cameraPosition;
	frag_texcoord = texcoord;
	frag_modelview = modelview;
	frag_position = position; 
	frag_lightIntensityArray = lightIntensityArray;
	frag_lightPositionArray = lightPositionArray;
	frag_reflectionCoefficient = reflectionCoefficient;
	frag_lightColorArray = lightColorArray;

	//transform normal to cameraSpace
	frag_normal = (modelview * vec4(normal,0)).xyz; //this should take the 3 first values of the 4f Vector, in CameraSpace

	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	gl_Position = projection * modelview * position;
}
