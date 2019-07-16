package pool;

/**
 *
 *
 *
 * @param <T>
 */
public interface Pool<T> {
    /**
     * 从对象池中请求一个对象，如果对象池内存在闲置的对象，（引用计数为0 的对象），则成功返回，
     * 并且该对象在池内的引用计数变为1，若对象池内所有对象都被引用，那么返回null值。
     *
     * @return 返回一个对象池内的对象
     */
    T borrow();

    /**
     * 从对象池中请求一个对象，如果对象池内存在闲置的对象，（引用计数为0 的对象），则成功返回，
     * 并且该对象在池内的引用计数变为1，若对象池内所有对象都被引用，那么抛出
     * {@code IllegalStateException} 对象池已满错误：
     *
     *
     * @return 返回一个对象池内的对象
     */
    T request();

    /**
     * 为该参数对象的引用计数增加1，该对象必须为对象内的对象
     *
     * @param t 请求增加引用计数的对象
     * @return 返回增加后的引用计数
     */
    int addReference(T t);

    /**
     * 为指针指向的对象的引用计数增加1，指针表示的是对象在对象池中的地址，也是对象在对象池内部
     * 维护的数组中的索引，通过{@code getPointer(T t)} 方法可以获得对象的指针
     *
     * @param pointer 请求增加引用计数的对象的指针
     * @return 返回增加后的引用计数
     */
    int addReference(int pointer);

    /**
     * 为该参数对象的引用计数减1，该对象必须为对象内的对象，当引用计数为0时，则认为该对象被对象
     * 池收回，并等待通过{@code borrow()}或{@code request()}方法再次被获取
     *
     * @param t 请求释放引用计数的对象
     * @return 返回减少后的引用计数
     */
    int release(T t);

    /**
     * 为指针指向的对象的引用计数增加1，指针表示的是对象在对象池中的地址，也是对象在对象池内部
     * 维护的数组中的索引，通过{@code getPointer(T t)} 方法可以获得对象的指针，当引用计数
     * 为0时，则认为该对象被对象池收回，并等待通过{@code borrow()}或{@code request()}
     * 方法再次被获取
     *
     * @param pointer 请求释放引用计数的对象的指针
     * @return 返回减少后的引用计数
     */
    int release(int pointer);

    /**
     * 获取对象的引用计数
     *
     * @param t 查询的对象
     * @return 对象的引用计数
     */
    int getCounter(T t);

    /**
     * 获取对象的引用计数
     *
     * @param pointer 查询的对象的指针
     * @return 对象的引用计数
     */
    int getCounter(int pointer);

    /**
     * 获取对象的指针，指针表示的是在对象池中的地址，也是对象在对象池内部维护的数组中的索引
     *
     * @param t 查询的对象
     * @return 指针
     */
    int getPointer(T t);

    /**
     * 根据指针获取对象
     *
     * @param pointer 指针
     * @return 指针对应的对象
     */
    T getElement(int pointer);

    /**
     * @return 对象池内部可被获取（引用计数为0）的对象总数
     */
    int availableAmount();

    /**
     * @return 对象池是否已满，若无可被获取的对象则返回true，否则返回false
     */
    boolean isFull();

    /**
     * @return 对象池的容量，即内部的对象总数
     */
    int size();
}
