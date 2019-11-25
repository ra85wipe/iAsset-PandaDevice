/*
 * ConnectedPropertyFactory.h
 *
 *      Author: wendel
 */

#ifndef AAS_BACKEND_SUBMODELELEMENT_PROPERTY_CONNECTEDPROPERTYFACTORY_H_
#define AAS_BACKEND_SUBMODELELEMENT_PROPERTY_CONNECTEDPROPERTYFACTORY_H_

#include "aas/submodelelement/property/IProperty.h"
#include "vab/core/proxy/IVABElementProxy.h"
#include "aas/ISubModel.h"
#include "backend/connected/aas/submodelelement/property/ConnectedContainerProperty.h"
#include "backend/connected/aas/submodelelement/property/ConnectedMapProperty.h"
#include "backend/connected/aas/submodelelement/property/ConnectedSingleProperty.h"
#include "aas/submodelelement/property/ISingleProperty.h"
#include "impl/metamodel/hashmap/aas/submodelelement/property/valuetypedef/PropertyValueTypeDef.h"


namespace basyx {
namespace aas {
namespace backend {
namespace connected {
namespace support {

using namespace submodelelement::property;

namespace ConnectedPropertyFactory {
// Forward declaration
static std::shared_ptr<ConnectedProperty> createSuitableProperty(std::shared_ptr<vab::core::proxy::IVABElementProxy> proxy, basyx::objectMap_t & originalPropertyMap, basyx::objectMap_t & valueTypeMap);
static std::shared_ptr<ConnectedProperty> createSingleProperty(std::shared_ptr<vab::core::proxy::IVABElementProxy> proxy, basyx::objectMap_t & originalPropertyMap);
static std::shared_ptr<ConnectedProperty> createProperty(std::shared_ptr<vab::core::proxy::IVABElementProxy> proxy)
{
  auto property = proxy->readElementValue("").Get<basyx::objectMap_t>();

  if ( property.find(SubmodelPaths::PROPERTIES) != property.end() )
  {
    return std::make_shared<ConnectedContainerProperty>(proxy);
  }

  auto valueTypePtr = property.find(PropertyPaths::VALUETYPE);
  // Check if valueType is set
  if ( valueTypePtr != property.end() )
  {
    auto valueType = valueTypePtr->second.Get<basyx::objectMap_t>();
    return createSuitableProperty(proxy, property, valueType);
  }

  // Property with no value type set
  if ( property.find(submodelelement::property::PropertyPaths::VALUE) != property.end() and property.find(qualifier::ReferablePaths::IDSHORT) != property.end() )
  {
    return createSingleProperty(proxy, property);
  }

  // If nothing suits return null
  return nullptr;
}

static std::shared_ptr<ConnectedProperty> createSuitableProperty(std::shared_ptr<vab::core::proxy::IVABElementProxy> proxy, basyx::objectMap_t & originalPropertyMap, basyx::objectMap_t & valueTypeMap)
{
  auto valueType_typeObject = valueTypeMap.at(impl::metamodel::PropertyValueTypeIdentifier::TYPE_OBJECT).Get<basyx::objectMap_t>();
  auto propertyValueTypeName = valueType_typeObject.at(impl::metamodel::PropertyValueTypeIdentifier::TYPE_NAME).GetStringContent();

  // if map -> create map
  if ( propertyValueTypeName.compare(impl::metamodel::PropertyValueTypeDef::Map) == 0 )
  {
    return std::make_shared<ConnectedMapProperty>(proxy);
  }

  // if collection -> create collection
  if ( propertyValueTypeName.compare(impl::metamodel::PropertyValueTypeDef::Collection) == 0 )
  {
    return std::make_shared<ConnectedCollectionProperty>(proxy);
  }

  // if no map and no collection -> must be single property
  else
  {
    return createSingleProperty(proxy, originalPropertyMap);
  }
}

static std::shared_ptr<ConnectedProperty> createSingleProperty(std::shared_ptr<vab::core::proxy::IVABElementProxy> proxy, basyx::objectMap_t & originalPropertyMap)
{
  ConnectedSingleProperty singleProperty(proxy);
  for ( auto & element : originalPropertyMap )
  {
    singleProperty.setLocalValue(element.first, element.second);
  }
  return std::make_shared<ConnectedCollectionProperty>(proxy);
}
}

}
}
}
}
}

#endif
