#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture map

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*

#define MAX_LIGHTS 8
// Uniform variables, set in main program
uniform mat4 projection;
uniform mat4 modelview;

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec4 position;
in vec2 texcoord;

// Output variables for fragment shader
out vec2 frag_texcoord;
out vec3 frag_normal;
out vec4 frag_position;

void main()
{
	// Pass texture coordinates, normals and vertex positions to fragment shader, OpenGL automatically
	// interpolates them to each pixel (in a perpectively correct manner)
	frag_normal = normal;
	
	frag_texcoord = texcoord;
	frag_position = position;
	
	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	gl_Position = projection * modelview * position;
}