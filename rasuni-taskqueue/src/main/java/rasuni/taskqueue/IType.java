package rasuni.taskqueue;

/**
 * Abstraction of various data types
 *
 * @param <JT>
 *            Java type correspond
 */
@SuppressWarnings({ "unused" })
public interface IType<JT>
{
	/**
	 * The string type
	 */
	public static final IType<String> STRING = new IType<String>()
			{
		@Override
		public <T> T visit(ITypeVisitor<T> visitor)
		{
			return visitor.string();
		}

		@Override
		public String toString()
		{
			return "string";
		}
			};

			/**
			 * Apply a visitor
			 * 
	 * @param <T>
	 *            visitor result type
	 * @param visitor
			 * @return the visitor
			 */
			<T> T visit(ITypeVisitor<T> visitor);
}
