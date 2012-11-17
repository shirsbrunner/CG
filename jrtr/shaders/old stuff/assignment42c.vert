#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture map

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*
uniform mat4 projection;
uniform mat4 modelview;

uniform vec4 lightDirection; //the lightDirection
uniform mat4 cameraMatrix; //the camera-matrix
#define MAXLIGHTS 5
uniform vec4 lightPositionArray[MAXLIGHTS]; //sh the following should create a vec4 array
uniform float lightIntensityArray[MAXLIGHTS]; //sh light intensities
uniform vec4 lightColorArray[MAXLIGHTS]; //sh light colors
uniform float reflectionCoefficient; //sh shininness
uniform float phongExponent; //sh Phong-Exponent
uniform float specularReflection; //sh specularReflection
uniform vec4 cameraPosition; //sh camera position

//this sets a default-Color for the non texture shaders
uniform vec4 color = vec4(0.0, 0.0, 0.0, 0.0);

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
out float frag_phongEx; //the phongExponent
out vec2 frag_texcoord;
out mat4 frag_modelview; //modelview & projection
out mat4 frag_cameraMatrix;
out float frag_lightIntensityArray[MAXLIGHTS];
out vec4 frag_lightPositionArray[MAXLIGHTS];


void main()
{	
	frag_cameraMatrix = cameraMatrix;	
	frag_phongEx = phongExponent;
	frag_specularReflection = specularReflection;
	frag_cameraPosition = cameraPosition;
	frag_texcoord = texcoord;
	frag_modelview = modelview; 
	frag_lightIntensityArray = lightIntensityArray;
	frag_lightPositionArray = lightPositionArray;
	
	vec4 temp_color = color;
	
	//transform normal to cameraSpace
	frag_normal = (cameraMatrix * vec4(normal,0)).xyz; //this should take the 3 first values of the 4f Vector, in CameraSpace
	
	//diffuse point light 
	ndotl = 0;
	for (int i = 0; i < MAXLIGHTS; i++){
	 	
	 	//light-Direction = light-position - pixel-position
	 	vec3 lightDirection = (cameraMatrix*vec4(lightPositionArray[i].xyz,1) - modelview*position).xyz;
	 	float ndotA = max(0,dot((modelview * vec4(normal,0)).xyz, normalize(lightDirection)));
	 	
	 	//add damping square dot-product: multiplicate with itself, looks good, but doesnt's maybe need the sqrt
	 	float lightIntensity = lightIntensityArray[i]/(dot(lightDirection,lightDirection));
	 	
	 	//put it all together
	 	ndotA = ndotA * reflectionCoefficient * lightIntensity;
	 	
	 	//add colors to the color
	 	temp_color += lightColorArray[i] * ndotA;
	 	
	 	//sum lightsources
	 	ndotl += ndotA;
	}

	frag_color = temp_color;

	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	gl_Position = projection * modelview * position;
}
