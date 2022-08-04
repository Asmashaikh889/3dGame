#version 400 core

in vec2 position;

out vec2 textureCoords1;
out vec2 textureCoords2;
out float blend;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec2 texOffset1;
uniform vec2 texOffset2;
uniform vec2 texCoordInfo;

void main(){

	vec2 fragTextureCoords = position + vec2(0.5, 0.5);
	fragTextureCoords.y = 1.0 - fragTextureCoords.y;
	fragTextureCoords /= texCoordInfo.x;
	textureCoords1 = fragTextureCoords + texOffset1;
	textureCoords2 = fragTextureCoords + texOffset2;
	blend = texCoordInfo.y;

	gl_Position = projectionMatrix * viewMatrix * vec4(position, 0.0, 1.0);


}