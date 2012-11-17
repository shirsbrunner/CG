#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture Map and a gloss map

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*
#define MAXLIGHTS 5
uniform vec4 lightPositionArray[MAXLIGHTS];
uniform mat4 projection;
uniform mat4 modelview;

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec4 position;
in vec2 texcoord;
in vec4 cameraPosition; //sh camera position

// Output variables for fragment shader
out vec3 frag_normal; //the normal
out mat4 frag_modelview; //modelview & projection
out vec4 frag_position;
out vec4 lightDirectionArray[MAXLIGHTS];
out float frag_distanceArray[MAXLIGHTS];
out vec4 frag_e;
out vec2 frag_texcoord;

void main()
{		
	frag_modelview = modelview;
	frag_position = position;
	
	for (int i =0; i<MAXLIGHTS; i++){
		lightDirectionArray[i] = normalize(vec4(lightPositionArray[i].x-position.x,lightPositionArray[i].y-position.y,lightPositionArray[i].z-position.z,0));
		
		//lightDistance: (sqrt(x^2+y^2+z^2)) 
	 	frag_distanceArray[i] = length(position-lightPositionArray[i]);//gl_FragCoord
	}
	
	frag_e = normalize(modelview*position-cameraPosition); //the e-vector for phong-shading

	//transform normal to cameraSpace
	frag_normal = (modelview * vec4(normal,0)).xyz; //this should take the 3 first values of the 4f Vector, in CameraSpace
	
	frag_texcoord = texcoord;

	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	gl_Position = projection * modelview * position;
}
