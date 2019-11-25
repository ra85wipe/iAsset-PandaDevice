/* ************************************************************************************************
 * BaSys Type System
 *
 * Define type IDs
 * ************************************************************************************************/

#ifndef BASYX_TYPES_H
#define BASYX_TYPES_H

#include <set>
#include <unordered_map>
#include <unordered_set>
#include <vector>
#include <bitset>

#include <basyx/any/any.h>

namespace basyx {
    using objectCollection_t = std::vector<basyx::any>;
    using objectMap_t = std::unordered_map<std::string, basyx::any>;

    template<class T>
    using specificCollection_t = std::vector<std::shared_ptr<T>>;

    template<class T>
    using specificMap_t = std::unordered_map<std::string, std::shared_ptr<T>>;

  //using objectSet_t = std::unordered_set<basyx::any>;
    using byte = uint8_t;
    using byte_array = std::vector<byte>;
};

enum BaSyxCommand {
	GET = 1,
	SET = 2,
	CREATE = 3,
	DEL = 4, // DELETE is a reserved keyword
	INVOKE = 5
};

#define BASYX_FRAMESIZE_SIZE 4
#define BASYX_STRINGSIZE_SIZE 4

#endif /* BASYX_TYPES_H */
