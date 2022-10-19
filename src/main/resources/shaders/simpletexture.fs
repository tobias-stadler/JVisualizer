#version 330 core

in vec2 TexCoord;

out vec4 col;

uniform sampler2D texture1;

void main(){

col = texture(texture1, TexCoord);

}