/**
 * <copyright>
 * </copyright>
 *
 * $Id: OrderAddressImpl.java,v 1.3 2008-09-18 12:57:07 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model1.impl;

import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderAddress;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Order Address</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.OrderAddressImpl#getOrderDetails <em>Order Details</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.OrderAddressImpl#getOrder <em>Order</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.OrderAddressImpl#getProduct <em>Product</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.OrderAddressImpl#getPrice <em>Price</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.OrderAddressImpl#isTestAttribute <em>Test Attribute</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class OrderAddressImpl extends AddressImpl implements OrderAddress
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected OrderAddressImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model1Package.Literals.ORDER_ADDRESS;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<OrderDetail> getOrderDetails()
  {
    return (EList<OrderDetail>)eGet(Model1Package.Literals.ORDER__ORDER_DETAILS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Order getOrder()
  {
    return (Order)eGet(Model1Package.Literals.ORDER_DETAIL__ORDER, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setOrder(Order newOrder)
  {
    eSet(Model1Package.Literals.ORDER_DETAIL__ORDER, newOrder);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Product1 getProduct()
  {
    return (Product1)eGet(Model1Package.Literals.ORDER_DETAIL__PRODUCT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setProduct(Product1 newProduct)
  {
    eSet(Model1Package.Literals.ORDER_DETAIL__PRODUCT, newProduct);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public float getPrice()
  {
    return ((Float)eGet(Model1Package.Literals.ORDER_DETAIL__PRICE, true)).floatValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setPrice(float newPrice)
  {
    eSet(Model1Package.Literals.ORDER_DETAIL__PRICE, new Float(newPrice));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isTestAttribute()
  {
    return ((Boolean)eGet(Model1Package.Literals.ORDER_ADDRESS__TEST_ATTRIBUTE, true)).booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setTestAttribute(boolean newTestAttribute)
  {
    eSet(Model1Package.Literals.ORDER_ADDRESS__TEST_ATTRIBUTE, new Boolean(newTestAttribute));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    if (baseClass == Order.class)
    {
      switch (derivedFeatureID)
      {
      case Model1Package.ORDER_ADDRESS__ORDER_DETAILS:
        return Model1Package.ORDER__ORDER_DETAILS;
      default:
        return -1;
      }
    }
    if (baseClass == OrderDetail.class)
    {
      switch (derivedFeatureID)
      {
      case Model1Package.ORDER_ADDRESS__ORDER:
        return Model1Package.ORDER_DETAIL__ORDER;
      case Model1Package.ORDER_ADDRESS__PRODUCT:
        return Model1Package.ORDER_DETAIL__PRODUCT;
      case Model1Package.ORDER_ADDRESS__PRICE:
        return Model1Package.ORDER_DETAIL__PRICE;
      default:
        return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    if (baseClass == Order.class)
    {
      switch (baseFeatureID)
      {
      case Model1Package.ORDER__ORDER_DETAILS:
        return Model1Package.ORDER_ADDRESS__ORDER_DETAILS;
      default:
        return -1;
      }
    }
    if (baseClass == OrderDetail.class)
    {
      switch (baseFeatureID)
      {
      case Model1Package.ORDER_DETAIL__ORDER:
        return Model1Package.ORDER_ADDRESS__ORDER;
      case Model1Package.ORDER_DETAIL__PRODUCT:
        return Model1Package.ORDER_ADDRESS__PRODUCT;
      case Model1Package.ORDER_DETAIL__PRICE:
        return Model1Package.ORDER_ADDRESS__PRICE;
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} // OrderAddressImpl
