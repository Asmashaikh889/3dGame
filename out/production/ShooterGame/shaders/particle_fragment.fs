#version 400 core

in vec2 textureCoords1;
in vec2 textureCoords2;
in float blend;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main(){

	vec4 color1 = texture(textureSampler, textureCoords1);
	vec4 color2 = texture(textureSampler, textureCoords2);

	fragColor = mix(color1, color2, blend);

}