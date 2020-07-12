/**
 * Copyright 2015 Yamato
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mod.ymt.sugar.cmn;

/**
 * @author Yamato
 *
 */
public class Coord3D implements Comparable<Coord3D> {
	public static final Coord3D ZERO = new Coord3D(0, 0, 0);

	public final int x, y, z;

	public Coord3D(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Coord3D add(Coord3D base) {
		return move(base.x, base.y, base.z);
	}

	@Override
	public int compareTo(Coord3D o) {
		int result = 0;
		if (result == 0)
			result = Utils.compare(y, o.y);
		if (result == 0)
			result = Utils.compare(x, o.x);
		if (result == 0)
			result = Utils.compare(z, o.z);
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coord3D other = (Coord3D) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

	public Coord3D[] getNeighbor() {
		return new Coord3D[]{
			move(-1, 0, 0), move(+1, 0, 0), move(0, -1, 0), move(0, +1, 0), move(0, 0, -1), move(0, 0, +1),
		};
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	public boolean isZero() {
		return x == 0 && y == 0 && z == 0;
	}

	public Coord3D move(int x, int y, int z) {
		return new Coord3D(this.x + x, this.y + y, this.z + z);
	}

	public boolean nearFrom(double distance, Coord3D from) {
		double dx = this.x - from.x;
		double dy = this.y - from.y;
		double dz = this.z - from.z;
		return dx * dx + dy * dy + dz * dz <= distance * distance;
	}

	public boolean nearInCube(int size, Coord3D from) {
		size /= 2;
		if (size < Math.abs(this.x - from.x))
			return false;
		if (size < Math.abs(this.y - from.y))
			return false;
		if (size < Math.abs(this.z - from.z))
			return false;
		return true;
	}

	public Coord3D rotate(int direction) {
		switch (direction & 3) {
			case 1:
				return new Coord3D(-this.z, this.y, +this.x);
			case 2:
				return new Coord3D(-this.x, this.y, -this.z);
			case 3:
				return new Coord3D(+this.z, this.y, -this.x);
		}
		return this;
	}

	public Coord3D subtract(Coord3D base) {
		return new Coord3D(this.x - base.x, this.y - base.y, this.z - base.z);
	}

	@Override
	public String toString() {
		return "Coord3D [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}
