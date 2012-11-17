#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture map

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*
uniform mat4 projection;
uniform mat4 modelview;

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec4 position;
in vec2 texcoord;

// Output variables for fragment shader
out vec3 frag_normal; //the normal
out mat4 frag_modelview; //modelview & projection
out vec4 frag_position;
out vec2 frag_texcoord;

void main()
{		
	frag_modelview = modelview;
	frag_position = position;

	//transform normal to cameraSpace
	frag_normal = (modelview * vec4(normal,0)).xyz; //this should take the 3 first values of the 4f Vector, in CameraSpace
	
	frag_texcoord = texcoord;

	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	gl_Position = projection * modelview * position;
}
