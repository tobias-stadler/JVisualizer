#version 330 core

layout (location = 0) in vec3 iPos;

uniform mat4 uTransformMatrix;

void main(){

    gl_Position = uTransformMatrix * vec4(iPos, 1.0f);

}