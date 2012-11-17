#version 150
// GLSL version 1.50
// Fragment shader phong perPixel-highlights

#define MAXLIGHTS 5
uniform vec4 lightPositionArray[MAXLIGHTS]; //sh the following should create a vec4 array
uniform float lightIntensityArray[MAXLIGHTS]; //sh light intensities
uniform vec4 lightColorArray[MAXLIGHTS]; //sh light colors
uniform float reflectionCoefficient; //sh shininness
uniform float phongExponent; //sh Phong-Exponent
uniform float specularReflection; //sh specularReflection
uniform vec4 cameraPosition; //sh camera position
uniform mat4 cameraMatrix;


// Variables passed in from the vertex shader
in vec3 frag_normal;
in vec4 frag_position;
in mat4 frag_modelview;

in vec4 frag_cameraPosition; //the position of the camera, where do we look from




// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	vec3 look_from_direction = - normalize((frag_modelview*frag_position).xyz);

	vec4 diffuse_light = vec4(0,0,0,0);
	vec4 specular_light = vec4(0,0,0,0);

	for (int i = 0; i<MAXLIGHTS; i++){
			vec3 light_direction = (cameraMatrix*vec4(lightPositionArray[i])-frag_modelview*frag_position).xyz;
			float radiance = lightIntensityArray[i]/dot(light_direction,light_direction);
			
			diffuse_light += radiance * reflectionCoefficient * max(0.0,dot(normalize((frag_modelview*vec4(frag_normal,0)).xyz), normalize(light_direction))) * lightColorArray[i];

			vec3 reflection_direction = reflect(-normalize(light_direction), normalize(frag_modelview * vec4(frag_normal,0)).xyz);
			specular_light += radiance * specularReflection * pow(max(dot(reflection_direction,look_from_direction),0.0),phongExponent)* lightColorArray[i];
	}

	frag_shaded = diffuse_light + specular_light; 	 	

}
