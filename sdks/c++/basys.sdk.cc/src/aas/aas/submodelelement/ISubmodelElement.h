/*
 * ISubmodelElement.h
 *
 *      Author: wendel
 */ 

#ifndef BASYX_METAMODEL_ISUBMODELELEMENT_H_
#define BASYX_METAMODEL_ISUBMODELELEMENT_H_


#include "aas/submodelelement/IElement.h"
#include "aas/qualifier/IHasDataSpecification.h"
#include "aas/qualifier/IReferable.h"
#include "aas/qualifier/qualifiable/IQualifiable.h"
#include "aas/qualifier/IHasSemantics.h"
#include "aas/qualifier/haskind/IHasKind.h"

namespace basyx {
namespace aas {
namespace submodelelement {

class ISubmodelElement : public qualifier::IHasDataSpecification, qualifier::IReferable, qualifier::qualifiable::IQualifiable, qualifier::IHasSemantics, qualifier::haskind::IHasKind
{
private:
  basyx::objectMap_t hashMap;
public:
  virtual ~ISubmodelElement() = default;
};

}
}
}

#endif
