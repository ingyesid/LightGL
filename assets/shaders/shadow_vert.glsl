/*
 * A phong vertex shader that supports a single light source with dynamic shadows.
 * Inspired by http://www.opengl-tutorial.org/intermediate-tutorials/tutorial-16-shadow-mapping/
 *
 * @author fabmax
 */

uniform mat4 uMvpMatrix;
uniform mat4 uModelMatrix;
uniform mat4 uViewMatrix;
uniform vec3 uLightDirection_worldspace;
uniform mat4 uShadowMvpMatrix;

attribute vec3 aVertexPosition_modelspace;
attribute vec3 aVertexNormal_modelspace;
attribute vec2 aVertexTexCoord;

varying vec2 vTexCoord;
varying vec3 vEyeDirection_cameraspace;
varying vec3 vLightDirection_cameraspace;
varying vec3 vNormal_cameraspace;
varying vec4 vShadowCoord;

void main() {
	// interpolate vertex color for usage in fragment shader
	vTexCoord = aVertexTexCoord;
	
	// compute vertex position in shadow map
	vShadowCoord = uShadowMvpMatrix * vec4(aVertexPosition_modelspace, 1);
	
	// Output position of the vertex in clip space : MVP * position
    gl_Position = uMvpMatrix * vec4(aVertexPosition_modelspace, 1);
    
	// Vector from vertex to camera, in camera space. In camera space, the camera is at the origin (0, 0, 0).
	vEyeDirection_cameraspace = -(uViewMatrix * uModelMatrix * vec4(aVertexPosition_modelspace, 1)).xyz;

	// Light direction, in camera space. M is left out because light position is already in world space.
	vLightDirection_cameraspace = (uViewMatrix * vec4(uLightDirection_worldspace, 0)).xyz;
	
	// Normal of the the vertex, in camera space
	vNormal_cameraspace = (uViewMatrix * uModelMatrix * vec4(aVertexNormal_modelspace, 0)).xyz;	
}