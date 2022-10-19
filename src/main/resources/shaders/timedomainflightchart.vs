#version 330 core
layout (location = 0) in float timeX;
layout (location = 1) in float valueY;

uniform mat4 transformMat;

void main(){
    gl_Position = transformMat * vec4(timeX, valueY, 0.0f, 1.0f);
}