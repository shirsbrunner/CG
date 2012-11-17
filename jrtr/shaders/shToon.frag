#version 150
// GLSL version 1.50
// Fragment shader for diffuse shading in combination with a texture map

// Uniform variables passed in from host program
uniform sampler2D myTexture;

// Variables passed in from the vertex shader
in float ndotl;
in vec4 frag_color;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	// The built-in GLSL function "texture" performs the texture lookup
	//shade depending on ndotl
	
	if (ndotl > 0.95f) frag_shaded = 1 * frag_color;
	else if (ndotl > 0.50f) frag_shaded = 0.5f * frag_color;
	else if (ndotl > 0.25f) frag_shaded = 0.25f * frag_color;
	else frag_shaded = 0.1f*frag_color;
	
	//frag_shaded = ndotl * frag_color;
		
}
