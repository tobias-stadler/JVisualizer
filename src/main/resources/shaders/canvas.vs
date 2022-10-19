#version 330 core
layout (location = 0) in vec3 iPos;
layout (location = 1) in vec2 iTexCoord;

out vec2 pTexCoord;

uniform mat4 uProjection;
uniform mat4 uModel;

void main(){
    gl_Position = uProjection * uModel * vec4(iPos, 1.0);
    pTexCoord = iTexCoord;
}