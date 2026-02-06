# JBCodec
A lightweight Java Byte Codec.

------

## Content
- [Content](#content)
- [Encoders](#encoders)
  - [Generated Data Block](#GeneratedDataBlock)
- [Decoders](#decoders)
- [Common Methods for Decoders and Encoders](#CommonMethodsforDecodersandEncoders)
- [CodecManager](#codecmanager)
- [Examples](#examples)
- [Compiling](#compiling)

------

## Encoder<T>
A `Encoder<T>` is used to encode an Object `T` to a `ByteBuffer`.
1. `ByteBuffer encoder(boolean head, T object)`: The `boolean head` specifies if the header should be included in the output. Returns the encoded object in a ByteBuffer;
2. `int estimateSize(boolean head, T obj)`: The `boolean head` specifies if the header should be included in the estimation, for some objects the size cannot be determined and will return -1 or 2 if the header is included.
3. `boolean confirmType(Object object)`: Returns true if the input object is an instance of the awaited type.

#### Generated Datablock

The returned ByteBuffer contains these values:
| TYPE | LENGTH | NAME | DESCRIPTION |
|------|--------|------|-------------|
|short | 2B     |HEADER| The header used to decode the following data, see [Decoder](#decoder).|
| x    |variable| DATA | The data of the block.|

For most generic types, the length is fixed: 
| TYPE | LENGTH | LENGTH w/ HEADER |
|------|--------|------------------|
| `java.lang.Byte` (byte) | 1 | 3 |
| `java.lang.Character` (char) | 1 | 3 |
| `java.lang.Short` (short) | 2 | 4 |
| `java.lang.Integer` (int) | 4 | 6 |
| `java.lang.Long` (long) | 8 | 10 |
| `java.lang.Double` (double) | 8 | 10 |
| `java.lang.Float` (float) | 8 | 10 |

Not all blocks need to contain data, a few data types do not contain any data, such as: `java.lang.Null`, `java.lang.Void`
| TYPE | LENGTH | LENGTH w/ HEADER |
|------|--------|------------------|
| `java.lang.Null` (null) | 0 | 2 |
| `java.lang.Void` (void) | 0 | 2 |

Note that encoding `Null` or `Void` types without their header is useless because they won't be decoded.<br>

Some types have variable sizes:
| TYPE | LENGTH | LENGTH w/ HEADER |
|------|--------|------------------|
| `java.lang.String` (String) | 2*length | 2*(length+1) |

Data Blocks can be recursive; a DataBlock representing an Array could contain the DataBlocks of all of it's children.<br>
Example for an Array of Longs:
```
- HEADER (array): 2B
- DATA (elements of the array): x*(8+2)B
  ...
|-- HEADER (long): 2B
|-- DATA (value of the long): 8B
  ...
```

------

## Decoder<T>
A `Decoder<T>` is used to decode a ByteBuffer input to the specified object `T`.
1. `T decode(boolean head, ByteBuffer input)  throws DecoderNotCompatibleException`: The `boolean head` specifies if the header should be verified, throws a `DecoderNotCompatibleException` if it isn't. Returns the decoded object from a ByteBuffer;

------

## Common Methods for Decoders and Encoders
1. `CodecManager codecManager()`: Returns the [CodecManager](#codecmanager) which the Decoder is registered to.
2. `short header()`: Returns the [Header](#data-blocks) which the Decoder is registered to.
3. `Class<?> type()`: Returns the Class which the Decoder is registered to.
4. `void verifyRegister() throws IllegalArgumentException`: Verifies if the Decoder/Encoder was already registered, if it is it throws an `IllegalArgumentException`

------

## CodecManager
The `CodecManager` class is responsible for managing the encoding and decoding of objects into and from `ByteBuffer` representations. It maintains a collection of registered encoders and decoders and provides methods to access and utilize them.
1. `void register(Decoder d, short header)`: This method registers a decoder with a specified header value.<br>`register(Encoder d, short header)`: This method registers an encoder with a specified header value.
2. `void register(Encoder e, Decoder d, short header)`: A convenience method to registers both an encoder and a decoder with the same header value.
3. `static CodecManager base()`: This static factory method creates and initializes a CodecManager instance with a set of base encoders and decoders for basic types such as byte, short, integer, double, float, long, character, string, array, and map. It returns the initialized CodecManager instance.
4. `Decoder getDecoder(short header)`: Gets the decoder registered for this specific header.
5. `Encoder getEncoder(String className)`: Gets the encoder registered with the specified class name (including package, `Class<>.getName()`).
6. `Encoder getEncoder(Object object)`: Gets the encoder associated with the current object type.
7. `ByteBuffer encode(Object obj)`: Encodes the object as a ByteBuffer; throws EncoderNotFoundException if a needed Encoder wasn't registered.
8. `ByteBuffer encode(boolean addHeader, Object obj)`: Encodes the object as a ByteBuffer; throws EncoderNotFoundException if a needed Encoder wasn't registered.
9. `Object decode(ByteBuffer bb)`: Decodes the ByteBuffer into an Object, the header needs to be present while encoding this ByteBuffer.


------

## Examples ([CarMain.java](https://github.com/UnKabaraQuiDev/jbcodec/blob/main/src/test/java/examples/car/CarMain.java))
```java
// Load the base CodecManager
CodecManager cm = CodecManager.base();

// Input value: Long 256
long in = 256;
// Encode the input value to a ByteBuffer
ByteBuffer bb = cm.encode(in);
// Print out the content of the ByteBuffer
System.out.println(PCUtils.byteBufferToHexString(bb));
// Output value: Long 256
long out = (long) cm.decode(bb);
System.out.println(in == out);
```
System.out output:
```
00 06      00 00 00 00 00 00 01 00
^ HEADER | ^ DATA 
(long) in == out: true
```

Creating a custom D/Encoder for the class Car:
```java
public class Car {
  int amountOfWheels;
  long capacity;
  boolean full;
  String name;
  @Override
  public String toString() {
    return amountOfWheels+", "+capacity+", "+full+", "+name;
  }
}

public class CarDecoder extends DefaultObjectDecoder<Car> {

  public CarDecoder() {
    super(Car.class);
  }

  @Override
  public Car decode(boolean head, ByteBuffer bb) {
    super.verifyHeader(head, bb);

    Car car = new Car();
    car.amountOfWheels = bb.getInt();
    car.capacity = bb.getLong();
    car.full = (boolean) cm.decode(bb);
    car.name = (String) cm.decode(bb);

    return car;
  }

}

public class CarEncoder extends DefaultObjectEncoder<Car> {

  public CarEncoder() {
    super(Car.class);
  }

  @Override
  public ByteBuffer encode(boolean head, Car obj) {
    ByteBuffer bb = ByteBuffer.allocate(estimateSize(head, obj));
    if (head)
      bb.putShort(header);

    bb.putInt(obj.amountOfWheels);
    bb.putLong(obj.capacity);
    bb.put(cm.encode(true, obj.full));
    bb.put(cm.encode(true, obj.name));

    bb.flip();
    return bb;
  }
  
  @Override
  public int estimateSize(boolean head, Car obj) {
    // header: 2B
    // amountOfWheels: 4B
    // capacity: 8B
    // full: estimateSize(Boolean)
    // name: estimateSize(String)
    return (head ? CodecManager.HEAD_SIZE : 0)+ 4 + 8 + 2 + cm.estimateSize(true, obj.full) + cm.estimateSize(true, obj.name);
  }
  
}
```

```java
cm.register(
    new CarEncoder(),
    new CarDecoder(),
    (short) 145
);

Car car = new Car();
car.amountOfWheels = 6;
car.capacity = 128;
car.full = true;
car.name = "Custom Car XXL";
System.out.println(car.toString());

ByteBuffer b2 = cm.encode(car);
System.out.println(PCUtils.byteBufferToHexString(b2));
Car carOut = (Car) cm.decode(b2);
System.out.println(carOut.toString());
System.out.println(car.equals(carOut));
```
System.out output:
```
6, 128, true, Custom Car XXL
00 91          00 00 00 06 00 00 00 00 00 00 00 80 00 0A              01          00 08             00 00 00 0E       43 75 73 74 6F 6D 20 43 61 72 20 58 58 4C 
^ CAR HEADER | ^ INT     | ^ LONG                | ^ BOOLEAN HEADER | ^ BOOLEAN | ^ STRING HEADER | ^ STRING LENGTH | ^ STRING
6, 128, true, Custom Car XXL
(Car) car == carOut: true
```
