package org.powertac.common.transformer

import org.powertac.common.Product
import org.powertac.common.enumerations.ProductType
import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * Product data is transmitted as just the enum value.
 * @author John Collins
 */
class ProductConverter implements SingleValueConverter
{

  @Override
  public boolean canConvert (Class type)
  {
    return type.equals(Product.class)
  }

  @Override
  public Object fromString (String pType)
  {
    return Product.findByProductType(ProductType.valueOf(pType))
  }

  @Override
  public String toString (Object product)
  {
    return product.productType
  }

}
