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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * 弱参照にて要素を保持する Collection です。
 * @author Yamato
 */
public class WeakCollection<E> extends AbstractCollection<E> implements Collection<E> {
	protected static final Logger logger = Logger.getLogger(WeakCollection.class.getName());
	protected final List<Reference<E>> inner = new ArrayList<Reference<E>>();

	/**
	 * value を弱参照にて WeakCollection に追加します。
	 */
	@Override
	public boolean add(E value) {
		if (value == null)
			throw new NullPointerException("WeakCollection value cant be null");
		return inner.add(new WeakReference<E>(value));
	}

	/**
	 * WeakCollection の要素に関係する反復子を返します。
	 * この Iterator の next は消失した参照に対して null を返します。
	 * その際、内部的に要素は remove されます。
	 */
	@Override
	public Iterator<E> iterator() {
		final Iterator<Reference<E>> iter = inner.iterator();
		return new Iterator<E>() {
			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public E next() {
				Reference<E> ref = iter.next();
				E result = ref.get();
				if (result == null) {
					iter.remove();
					logger.fine("delete Reference");
				}
				return result;
			}

			@Override
			public void remove() {
				iter.remove();
			}
		};
	}

	/**
	 * 現在のコレクションサイズを返します。
	 * この値にはすでに消失した参照も含むため、実際のオブジェクト数はこれより少ない可能性があります。
	 */
	@Override
	public int size() {
		return inner.size();
	}
}
