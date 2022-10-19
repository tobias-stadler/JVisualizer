#version 330 core

in vec2 pTexCoord;

out vec4 oFragColor;

uniform sampler2D uTexture;

void main(){

oFragColor = texture(uTexture, pTexCoord);

}