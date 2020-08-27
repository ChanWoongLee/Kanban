import axios from "axios";
export function post(url, parm) {
  axios.post(url, parm).then((result) => {
    return result.data;
  });
}
export async function put(url, parm) {
  await axios.put(url, parm).then((result) => {
    return result.data;
  });
}
export function remove(url, param) {
  try {
    let response = axios.delete(url, parm);
    console.log(response);
    return response;
  } catch (error) {
    console.log(error);
  }
}

export function get(url, param) {
  let response;
  axios.get(url).then((result) => {
    response = result;
  });
}
