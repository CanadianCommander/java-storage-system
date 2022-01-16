package ca.bbenetti.jss.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface Converter<F,T>
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * convert an object from (F) to type (T).
	 * @param from - object to convert from
	 * @return - object of type T.
	 */
	public abstract T convert(F from);

	/**
	 * convert an object from (F) to type (T).
	 * @param from - object to convert from
	 * @return - object of type T.
	 */
	default T convert(Optional<F> from)
	{
		return this.convert(from.orElse(null));
	}

	/**
	 * convert a list of items.
	 * @param from - the type to convert from
	 * @return - a list of the target type
	 */
	default List<T> convert(List<F> from)
	{
		return Optional.ofNullable(from)
		               .map((fromLst) -> fromLst.stream().map(this::convert).collect(Collectors.toList()))
		               .orElse(new ArrayList<>());
	}
}
