/*
 * MathJax.hpp
 *
 * Copyright (C) 2009-12 by RStudio, Inc.
 *
 * This program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */

#include <string>
#include <vector>
#include <map>

#include <boost/utility.hpp>
#include <boost/regex.hpp>
#include <boost/function.hpp>

namespace core {
namespace markdown {

struct ExcludePattern
{
   ExcludePattern(const boost::regex& pattern)
      : begin(pattern)
   {
   }

   ExcludePattern(const boost::regex& beginPattern,
                  const boost::regex& endPattern)
      : begin(beginPattern), end(endPattern)
   {
   }

   boost::regex begin;
   boost::regex end;
};


struct MathBlock
{
   MathBlock(const std::string& equation,
             const std::string& suffix)
      : equation(equation), suffix(suffix)
   {
   }

   std::string equation;
   std::string suffix;
};

class MathJaxFilter : boost::noncopyable
{
public:
   MathJaxFilter(const std::vector<ExcludePattern>& excludePatterns,
                 std::string* pInput,
                 std::string* pHTMLOutput);
   ~MathJaxFilter();

private:
   void filter(const boost::regex& re,
               std::string* pInput,
               std::map<std::string,MathBlock>* pMathBlocks)
   {
      filter(re,
             boost::function<bool(const std::string&)>(),
             pInput,
             pMathBlocks);
   }

   void filter(const boost::regex& re,
               const boost::function<bool(const std::string&)>& condition,
               std::string* pInput,
               std::map<std::string,MathBlock>* pMathBlocks);

   std::string substitute(
               const boost::function<bool(const std::string&)>& condition,
               boost::match_results<std::string::const_iterator> match,
               std::map<std::string,MathBlock>* pMathBlocks);

   void restore(const std::map<std::string,MathBlock>::value_type& block,
                const std::string& beginDelim,
                const std::string& endDelim);

private:
   std::string* pHTMLOutput_;
   std::map<std::string,MathBlock> displayMathBlocks_;
   std::map<std::string,MathBlock> inlineMathBlocks_;
};

bool requiresMathjax(const std::string& htmlOutput);


} // namespace markdown
} // namespace core
   


