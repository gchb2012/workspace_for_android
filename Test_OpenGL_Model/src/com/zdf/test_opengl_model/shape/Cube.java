package com.zdf.test_opengl_model.shape;

public class Cube extends Mesh {
	public Cube(float width, float height, float depth) {
        width  /= 2;
        height /= 2;
        depth  /= 2;
 
        float vertices[] = { -width, -height, -depth, // 0
                              width, -height, -depth, // 1
                              width,  height, -depth, // 2
                             -width,  height, -depth, // 3
                             -width, -height,  depth, // 4
                              width, -height,  depth, // 5
                              width,  height,  depth, // 6
                             -width,  height,  depth, // 7
        };
 
        short indices[] = { 0, 5, 4,
			                0, 1, 5,
			                1, 2, 6,
			                1, 6, 5,
			                2, 3, 7,
			                2, 7, 6,
			                3, 0, 4,
			                3, 4, 7,
			                4, 5, 6,
			                4, 6, 7,
			                3, 2, 1,
			                3, 1, 0, };

		float[] colors = {
			 1f, 0f, 0f, 1f,
			 0f, 1f, 0f, 1f,
			 0f, 0f, 1f, 1f,
			 1f, 0f, 1f, 1f,
			 1f, 0f, 0f, 1f,
			 0f, 1f, 0f, 1f,
			 0f, 0f, 1f, 1f,
			 1f, 0f, 1f, 1f,
		};
 
        setVertices(vertices);
        setIndices(indices);
        setColors(colors);
    }
}
