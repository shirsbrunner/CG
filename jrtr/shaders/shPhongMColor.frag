#version 150
// GLSL version 1.50
// Fragment shader phong perPixel-highlights


#define MAX_LIGHTS 8
// Fragment shader for diffuse shading in combination with a texture map

// Uniform variables passed in from host program
uniform sampler2D Texture0;
uniform mat4 modelview;
uniform mat4 cameraMatrix; //maybe
uniform float lightIntensityArray[MAX_LIGHTS];
uniform vec4 lightPositionArray[MAX_LIGHTS];
uniform vec4 lightColorArray[MAX_LIGHTS];
uniform float reflectionCoefficient;
uniform float specularReflection;
uniform float phongExponent;
uniform vec3 cameraPosition;


// Variables passed in from the vertex shader

in vec2 frag_texcoord;
in vec3 frag_normal;
in vec4 frag_position;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{

	vec3 look_from_direction = - normalize((modelview*frag_position).xyz);
	
	vec4 diffuse_light = vec4(0,0,0,0);
	vec4 specular_light = vec4(0,0,0,0);
	
	for (int i = 0; i<MAX_LIGHTS; i++){
		vec3 light_direction = (cameraMatrix*vec4(lightPositionArray[i])-modelview*frag_position).xyz;
		float radiance = lightIntensityArray[i]/dot(light_direction,light_direction);
		diffuse_light += radiance * reflectionCoefficient * max(0.0,dot(normalize((modelview*vec4(frag_normal,0)).xyz), normalize(light_direction))) * vec4(lightColorArray[i]);
		
		vec3 reflection_direction = reflect(-normalize(light_direction), normalize(modelview * vec4(frag_normal,0)).xyz);
		specular_light += radiance * specularReflection * pow(max(dot(reflection_direction,look_from_direction),0.0),phongExponent);
	}
	
	// The built-in GLSL function "texture" performs the texture lookup
	//frag_shaded = (diffuse_light + specular_light) * (texture(myTexture, frag_texcoord) + 0.5);
	frag_shaded = diffuse_light + specular_light+0.05;
}
