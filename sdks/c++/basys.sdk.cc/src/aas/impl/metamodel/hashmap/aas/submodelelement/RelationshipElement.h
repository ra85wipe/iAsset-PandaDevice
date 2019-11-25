/*
 * RelationshipElement.h
 *
 *      Author: wendel
 */

#ifndef IMPL_METAMODEL_MAP_AAS_SUBMODELELEMENT_RELATIONSHIPELEMENT_H_
#define IMPL_METAMODEL_MAP_AAS_SUBMODELELEMENT_RELATIONSHIPELEMENT_H_

#include "aas/submodelelement/IRelationshipElement.h"
#include "aas/reference/IReference.h"
#include "SubmodelElement.h"

namespace basyx {
namespace submodel {
namespace metamodel {
namespace map {
namespace submodelelement {

class RelationshipElement : public SubmodelElement, public aas::submodelelement::IRelationshipElement
{
public:
  ~RelationshipElement() = default;

  //constructors
  RelationshipElement();
  RelationshipElement(const std::shared_ptr<aas::reference::IReference> & first, const std::shared_ptr<aas::reference::IReference> & second);

  // Inherited via IRelationshipElement
  virtual void setFirst(const std::shared_ptr<aas::reference::IReference>& first) override;
  virtual std::shared_ptr<aas::reference::IReference> getFirst() const override;
  virtual void setSecond(const std::shared_ptr<aas::reference::IReference>& second) override;
  virtual std::shared_ptr<aas::reference::IReference> getSecond() const override;

private:
  std::shared_ptr<aas::reference::IReference> first, second;
};

}
}
}
}
}

#endif
